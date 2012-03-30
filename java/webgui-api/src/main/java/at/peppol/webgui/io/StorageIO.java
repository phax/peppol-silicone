/**
 * Copyright (C) 2006-2012 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.peppol.webgui.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.EAppend;
import com.phloc.commons.io.IReadWriteResource;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.string.StringHelper;

/**
 * Storage IO class
 * 
 * @author philip
 */
@NotThreadSafe
public final class StorageIO {
  private static final Logger s_aLogger = LoggerFactory.getLogger (StorageIO.class);
  private static String s_sBasePath;

  private StorageIO () {}

  public static boolean isBasePathInited () {
    return s_sBasePath != null;
  }

  public static void initBasePath (@Nonnull @Nonempty final String sRealPath) {
    if (StringHelper.hasNoText (sRealPath))
      throw new IllegalArgumentException ("realPath");
    if (s_sBasePath != null)
      throw new IllegalStateException ("another real path is already present!");
    s_aLogger.info ("Using '" + sRealPath + "' as the storage base");
    s_sBasePath = sRealPath;
  }

  @Nonnull
  @Nonempty
  public static String getBasePath () {
    if (s_sBasePath == null)
      throw new IllegalStateException ("Base path was not initialized!");
    return s_sBasePath;
  }

  @Nonnull
  public static File getFile (final String sBasePathRelativePath) {
    return new File (getBasePath (), sBasePathRelativePath);
  }

  @Nonnull
  public static IReadWriteResource getResource (final String sBasePathRelativePath) {
    return new FileSystemResource (getFile (sBasePathRelativePath));
  }

  @Nonnull
  public static InputStream getInputStream (final String sBasePathRelativePath) {
    return getResource (sBasePathRelativePath).getInputStream ();
  }

  @Nonnull
  public static OutputStream getOutputStream (final String sBasePathRelativePath, @Nonnull final EAppend eAppend) {
    return getResource (sBasePathRelativePath).getOutputStream (eAppend);
  }
}
