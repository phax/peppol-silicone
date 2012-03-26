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
package ap.peppol.webgui.api.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.io.IReadWriteResource;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.io.resource.FileSystemResource;

/**
 * Storage IO class
 * 
 * @author philip
 */
@Immutable
public final class StorageIO {
  private static String s_sBasePath;

  private StorageIO () {}

  public static void initBasePath (final String sRealPath) {
    if (s_sBasePath != null)
      throw new IllegalStateException ("another real path is already present!");
    s_sBasePath = sRealPath;
  }

  @Nonnull
  public static File getFile (final String sPath) {
    return new File (s_sBasePath, sPath);
  }

  @Nonnull
  public static IReadWriteResource getBasePathRelativeResource (final String sPath) {
    return new FileSystemResource (getFile (sPath));
  }

  @Nonnull
  public static InputStream getBasePathRelativeInputStream (final String sPath) {
    return FileUtils.getInputStream (getFile (sPath));
  }

  @Nonnull
  public static OutputStream getBasePathRelativeOutputStream (final String sPath) {
    return FileUtils.getOutputStream (getFile (sPath));
  }
}
