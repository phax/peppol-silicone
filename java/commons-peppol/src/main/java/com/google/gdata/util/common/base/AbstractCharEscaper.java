/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gdata.util.common.base;

/**
 * An object that converts literal text into a format safe for inclusion in a
 * particular context (such as an XML document). Typically (but not always), the
 * inverse process of "unescaping" the text is performed automatically by the
 * relevant parser.
 * <p>
 * For example, an XML escaper would convert the literal string
 * {@code "Foo<Bar>"} into {@code "Foo&lt;Bar&gt;"} to prevent {@code "<Bar>"}
 * from being confused with an XML tag. When the resulting XML document is
 * parsed, the parser API will return this text as the original literal string
 * {@code "Foo<Bar>"}.
 * <p>
 * A {@code CharEscaper} instance is required to be stateless, and safe when
 * used concurrently by multiple threads.
 * <p>
 * Several popular escapers are defined as constants in the class
 * {@link CharEscapers}. To create your own escapers, use
 * {@link CharEscaperBuilder}, or extend this class and implement the
 * {@link #escape(char)} method.
 */
public abstract class AbstractCharEscaper implements IEscaper {
  /**
   * Returns the escaped form of a given literal string.
   * 
   * @param string
   *        the literal string to be escaped
   * @return the escaped form of {@code string}
   * @throws NullPointerException
   *         if {@code string} is null
   */
  public String escape (final String string) {
    // Inlineable fast-path loop which hands off to escapeSlow() only if needed
    final int length = string.length ();
    for (int index = 0; index < length; index++) {
      if (escape (string.charAt (index)) != null) {
        return escapeSlow (string, index);
      }
    }
    return string;
  }

  /**
   * Returns the escaped form of a given literal string, starting at the given
   * index. This method is called by the {@link #escape(String)} method when it
   * discovers that escaping is required. It is protected to allow subclasses to
   * override the fastpath escaping function to inline their escaping test. See
   * {@link CharEscaperBuilder} for an example usage.
   * 
   * @param s
   *        the literal string to be escaped
   * @param pindex
   *        the index to start escaping from
   * @return the escaped form of {@code string}
   * @throws NullPointerException
   *         if {@code string} is null
   */
  protected String escapeSlow (final String s, final int pindex) {
    final int slen = s.length ();
    int index = pindex;

    // Get a destination buffer and setup some loop variables.
    char [] dest = DEST_TL.get ();
    int destSize = dest.length;
    int destIndex = 0;
    int lastEscape = 0;

    // Loop through the rest of the string, replacing when needed into the
    // destination buffer, which gets grown as needed as well.
    for (; index < slen; index++) {

      // Get a replacement for the current character.
      final char [] r = escape (s.charAt (index));

      // If no replacement is needed, just continue.
      if (r == null)
        continue;

      final int rlen = r.length;
      final int charsSkipped = index - lastEscape; // Characters we skipped
                                                   // over.

      // This is the size needed to add the replacement, not the full
      // size needed by the string. We only regrow when we absolutely must.
      final int sizeNeeded = destIndex + charsSkipped + rlen;
      if (destSize < sizeNeeded) {
        destSize = sizeNeeded + (slen - index) + DEST_PAD;
        dest = growBuffer (dest, destIndex, destSize);
      }

      // If we have skipped any characters, we need to copy them now.
      if (charsSkipped > 0) {
        s.getChars (lastEscape, index, dest, destIndex);
        destIndex += charsSkipped;
      }

      // Copy the replacement string into the dest buffer as needed.
      if (rlen > 0) {
        System.arraycopy (r, 0, dest, destIndex, rlen);
        destIndex += rlen;
      }
      lastEscape = index + 1;
    }

    // Copy leftover characters if there are any.
    final int charsLeft = slen - lastEscape;
    if (charsLeft > 0) {
      final int sizeNeeded = destIndex + charsLeft;
      if (destSize < sizeNeeded) {

        // Regrow and copy, expensive! No padding as this is the final copy.
        dest = growBuffer (dest, destIndex, sizeNeeded);
      }
      s.getChars (lastEscape, slen, dest, destIndex);
      destIndex = sizeNeeded;
    }
    return new String (dest, 0, destIndex);
  }

  /**
   * Returns the escaped form of the given character, or {@code null} if this
   * character does not need to be escaped. If an empty array is returned, this
   * effectively strips the input character from the resulting text.
   * <p>
   * If the character does not need to be escaped, this method should return
   * {@code null}, rather than a one-character array containing the character
   * itself. This enables the escaping algorithm to perform more efficiently.
   * <p>
   * An escaper is expected to be able to deal with any {@code char} value, so
   * this method should not throw any exceptions.
   * 
   * @param c
   *        the character to escape if necessary
   * @return the replacement characters, or {@code null} if no escaping was
   *         needed
   */
  protected abstract char [] escape (char c);

  /**
   * Helper method to grow the character buffer as needed, this only happens
   * once in a while so it's ok if it's in a method call. If the index passed in
   * is 0 then no copying will be done.
   */
  private static final char [] growBuffer (final char [] dest, final int index, final int size) {
    final char [] copy = new char [size];
    if (index > 0) {
      System.arraycopy (dest, 0, copy, 0, index);
    }
    return copy;
  }

  /**
   * The amount of padding to use when growing the escape buffer.
   */
  private static final int DEST_PAD = 32;

  /**
   * A thread-local destination buffer to keep us from creating new buffers. The
   * starting size is 1024 characters. If we grow past this we don't put it back
   * in the threadlocal, we just keep going and grow as needed.
   */
  private static final ThreadLocal <char []> DEST_TL = new ThreadLocal <char []> () {
    @Override
    protected char [] initialValue () {
      return new char [1024];
    }
  };
}
