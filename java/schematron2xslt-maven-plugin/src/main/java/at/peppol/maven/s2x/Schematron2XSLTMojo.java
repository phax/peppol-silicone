/**
 * Copyright (C) 2010 Bundesrechenzentrum GmbH
 * http://www.brz.gv.at
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

package at.peppol.maven.s2x;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

import at.peppol.validation.schematron.xslt.ISchematronXSLTProvider;
import at.peppol.validation.schematron.xslt.SchematronResourceSCHCache;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

/**
 * Converts one or more Schematron schema files into XSLT scripts.
 * 
 * @goal convert
 * @phase generate-resources
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class Schematron2XSLTMojo extends AbstractMojo {
  /**
   * The Maven Project.
   * 
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * The directory where the Schematron files reside.
   * 
   * @parameter property="schematronDirectory"
   *            expression="${basedir}/src/main/schematron"
   */
  private File m_aSchematronDirectory;

  /**
   * The file extension of the Schematron files. Files that match the extension
   * will be converted. Only files in the schematronDirectory will be
   * considered, not those in subdirectories. The default value is ".sch".
   * <p>
   * This parameter cannot be used in combination with schematronPattern. If
   * neither is specified, schematronExtension will be used with its default
   * value.
   * 
   * @parameter expression="${schematronExtension}" Plugin developers note: the
   *            default value is not specified using the parameter annotation
   *            because otherwise the plugin cannot detect whether or not a
   *            value for this parameter was specified.
   */
  private String schematronExtension;

  /**
   * A pattern for the Schematron files. Can contain Ant-style wildcards and
   * double wildcards. All files that match the pattern will be converted. Files
   * in the schematronDirectory and its subdirectories will be considered.
   * <p>
   * This parameter cannot be used in combination with schematronExtension. If
   * neither is specified, schematronExtension will be used with its default
   * value.
   * 
   * @parameter expression="${schematronPattern}" Plugin developers note: the
   *            default value is not specified using the parameter annotation
   *            because otherwise the plugin cannot detect whether or not a
   *            value for this parameter was specified.
   */
  private String schematronPattern;

  /**
   * The directory where the XSLT files will be saved.
   * 
   * @required
   * @parameter property="xsltDirectory" expression="${basedir}/src/main/xslt"
   */
  private File m_aXSLTDirectory;

  /**
   * The file extension of the XSLT files.
   * 
   * @parameter property="xsltExtension" expression="${xsltExtension}"
   *            default-value=".xslt"
   */
  private String m_sXSLTExtension;

  /**
   * Overwrite existing Schematron files without notice?
   * 
   * @parameter property="overwrite" default-value="true"
   */
  private boolean m_bOverwriteWithoutQuestion = true;

  public void setSchematronDirectory (final File aDir) {
    m_aSchematronDirectory = aDir;
    if (!m_aSchematronDirectory.isAbsolute ())
      m_aSchematronDirectory = new File (project.getBasedir (), aDir.getPath ());
    getLog ().debug ("Searching Schematron files in the directory '" + m_aSchematronDirectory + "'");
  }

  public void setSchematronExtension (final String sExt) {
    schematronExtension = sExt;
    getLog ().debug ("Setting Schematron file extension to '" + sExt + "'");
  }

  public void setSchematronPattern (final String sPattern) {
    schematronPattern = sPattern;
    getLog ().debug ("Setting Schematron pattern to '" + sPattern + "'");
  }

  public void setXsltDirectory (final File aDir) {
    m_aXSLTDirectory = aDir;
    if (!m_aXSLTDirectory.isAbsolute ())
      m_aXSLTDirectory = new File (project.getBasedir (), aDir.getPath ());
    getLog ().debug ("Writing XSLT files into directory '" + m_aXSLTDirectory + "'");
  }

  public void setXsltExtension (final String sExt) {
    m_sXSLTExtension = sExt;
    getLog ().debug ("Setting XSLT file extension to '" + sExt + "'");
  }

  public void setOverwrite (final boolean bOverwrite) {
    m_bOverwriteWithoutQuestion = bOverwrite;
    if (m_bOverwriteWithoutQuestion)
      getLog ().debug ("Overwriting XSLT files without notice");
    else
      getLog ().debug ("Ignoring existing Schematron files");
  }

  public void execute () throws MojoExecutionException, MojoFailureException {
    if (m_aSchematronDirectory == null)
      throw new MojoExecutionException ("No Schematron directory specified!");
    if (m_aSchematronDirectory.exists () && !m_aSchematronDirectory.isDirectory ())
      throw new MojoExecutionException ("The specified Schematron directory " +
                                        m_aSchematronDirectory +
                                        " is not a directory!");
    final boolean bSchematronExtensionSpecified = schematronExtension != null && !schematronExtension.isEmpty ();
    final boolean bSchematronPatternSpecified = schematronPattern != null && !schematronPattern.isEmpty ();
    if (bSchematronExtensionSpecified && bSchematronPatternSpecified) {
      throw new MojoExecutionException ("Schematron extension ('" +
                                        schematronExtension +
                                        "') and Schematron pattern ('" +
                                        schematronPattern +
                                        "') were both specified!");
    }
    if (bSchematronExtensionSpecified && !schematronExtension.startsWith ("."))
      throw new MojoExecutionException ("The Schematron extension '" + schematronExtension + "' is invalid!");
    if (!bSchematronExtensionSpecified && !bSchematronPatternSpecified) {
      schematronPattern = "*.sch";
      getLog ().debug ("Neither schematronExtension nor schematronPattern were specified");
    }
    if (bSchematronExtensionSpecified) {
      schematronPattern = "*" + schematronExtension;
    }
    getLog ().debug ("Using schematronPattern '" + schematronPattern + "'");
    if (m_aXSLTDirectory == null)
      throw new MojoExecutionException ("No XSLT directory specified!");
    if (m_aXSLTDirectory.exists () && !m_aXSLTDirectory.isDirectory ())
      throw new MojoExecutionException ("The specified XSLT directory " + m_aXSLTDirectory + " is not a directory!");
    if (m_sXSLTExtension == null || m_sXSLTExtension.length () == 0 || !m_sXSLTExtension.startsWith ("."))
      throw new MojoExecutionException ("The XSLT extension '" + m_sXSLTExtension + "' is invalid!");
    if (!m_aXSLTDirectory.exists () && !m_aXSLTDirectory.mkdirs ())
      throw new MojoExecutionException ("Failed to create the XSLT directory " + m_aXSLTDirectory);

    // for all Schematron files that match the pattern
    final DirectoryScanner aDirScanner = new DirectoryScanner ();
    aDirScanner.setBasedir (m_aSchematronDirectory);
    aDirScanner.setIncludes (new String [] { schematronPattern });
    aDirScanner.setCaseSensitive (true);
    aDirScanner.scan ();
    final String [] aFilenames = aDirScanner.getIncludedFiles ();
    if (aFilenames != null) {
      for (final String sFilename : aFilenames) {
        final File aFile = new File (m_aSchematronDirectory, sFilename);

        // 1. build XSLT file name (outputdir + localpath with new extension)
        final File aXSLTFile = new File (m_aXSLTDirectory, FilenameHelper.getWithoutExtension (sFilename) +
                                                           m_sXSLTExtension);

        getLog ().info ("Converting Schematron file '" +
                        aFile.getPath () +
                        "' to XSLT file '" +
                        aXSLTFile.getPath () +
                        "'");

        // 2. The Schematron resource
        final IReadableResource aSchematronResource = new FileSystemResource (aFile);

        // 3. Check if the XSLT file already exists
        if (aXSLTFile.exists () && !m_bOverwriteWithoutQuestion) {
          // 3.1 Not overwriting the existing file
          getLog ().debug ("Skipping XSLT file '" + aXSLTFile.getPath () + "' because it already exists!");
        }
        else {
          // 3.2 Okay, write the XSLT file
          try {
            final ISchematronXSLTProvider aPreprocessor = SchematronResourceSCHCache.createSchematronXSLTProvider (aSchematronResource,
                                                                                                                   null,
                                                                                                                   null);
            if (aPreprocessor != null) {
              XMLWriter.writeToStream (aPreprocessor.getXSLTDocument (),
                                       new FileOutputStream (aXSLTFile),
                                       XMLWriterSettings.DEFAULT_XML_SETTINGS);
            }
            else {
              final String message = "Failed to convert '" + aFile.getPath () + "': the Schematron resource is invalid";
              getLog ().error (message);
              throw new MojoFailureException (message);
            }
          }
          catch (final MojoFailureException up) {
            throw up;
          }
          catch (final Exception ex) {
            final String sMessage = "Failed to convert '" +
                                    aFile.getPath () +
                                    "' to XSLT file '" +
                                    aXSLTFile.getPath () +
                                    "'";
            getLog ().error (sMessage, ex);
            throw new MojoFailureException (sMessage, ex);
          }
        }
      }
    }
  }
}
