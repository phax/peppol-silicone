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

package eu.peppol.validation.maven.s2x;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

import eu.peppol.validation.commons.schematron.xslt.ISchematronXSLTProvider;
import eu.peppol.validation.commons.schematron.xslt.SchematronResourceSCHCache;

/**
 * @goal convert
 * @phase generate-resources
 * @description Convert one or more XVML files into Schematron files
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
   * The file extension of the Schematron files.
   * 
   * @parameter property="schematronExtension"
   *            expression="${schematronExtension}" default-value=".sch"
   */
  private String m_sSchematronExtension;

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
    m_sSchematronExtension = sExt;
    getLog ().debug ("Setting Schematron file extension to '" + sExt + "'");
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

  public void execute () throws MojoExecutionException {
    if (m_aSchematronDirectory == null)
      throw new MojoExecutionException ("No Schematron directory specified!");
    if (m_aSchematronDirectory.exists () && !m_aSchematronDirectory.isDirectory ())
      throw new MojoExecutionException ("The specified Schematron directory " +
                                        m_aSchematronDirectory +
                                        " is not a directory!");
    if (m_sSchematronExtension == null ||
        m_sSchematronExtension.length () == 0 ||
        !m_sSchematronExtension.startsWith ("."))
      throw new MojoExecutionException ("The Schematron extension '" + m_sSchematronExtension + "' is invalid!");
    if (m_aXSLTDirectory == null)
      throw new MojoExecutionException ("No XSLT directory specified!");
    if (m_aXSLTDirectory.exists () && !m_aXSLTDirectory.isDirectory ())
      throw new MojoExecutionException ("The specified XSLT directory " + m_aXSLTDirectory + " is not a directory!");
    if (m_sXSLTExtension == null || m_sXSLTExtension.length () == 0 || !m_sXSLTExtension.startsWith ("."))
      throw new MojoExecutionException ("The XSLT extension '" + m_sXSLTExtension + "' is invalid!");

    if (!m_aXSLTDirectory.exists () && !m_aXSLTDirectory.mkdirs ())
      throw new MojoExecutionException ("Failed to create the XSLT directory " + m_aXSLTDirectory);

    // for all Schematron files in the Schematron directory
    final File [] aFiles = m_aSchematronDirectory.listFiles ();
    if (aFiles != null)
      for (int i = 0; i < aFiles.length; ++i) {
        final File aFile = aFiles[i];
        if (aFile.isFile () && aFile.getName ().endsWith (m_sSchematronExtension)) {
          getLog ().info ("Converting Schematron file " + aFile.getName ());

          // 1. The Schematron resource
          final IReadableResource aSchematronResource = new FileSystemResource (aFile);

          // 2. build XSLT file name (dir + basename + extension)
          final File aXSLTFile = new File (m_aXSLTDirectory, FilenameHelper.getWithoutExtension (aFile.getName ()) +
                                                             m_sXSLTExtension);

          // 3. Check if the XSLT file already exists
          if (aXSLTFile.exists () && !m_bOverwriteWithoutQuestion) {
            // 3.1 Not overwriting the existing file
            getLog ().debug ("Skipping XSLT file " + aXSLTFile.getName () + " because it already exists!");
          }
          else {
            // 3.2 Okay, write the XSLT file
            getLog ().debug ("Writing XSLT file " + aXSLTFile.getName ());
            try {
              final ISchematronXSLTProvider aPreprocessor = SchematronResourceSCHCache.createSchematronXSLTProvider (aSchematronResource,
                                                                                                                     null,
                                                                                                                     null);
              XMLWriter.writeToStream (aPreprocessor.getXSLTDocument (),
                                       new FileOutputStream (aXSLTFile),
                                       XMLWriterSettings.DEFAULT_XML_SETTINGS);
            }
            catch (final Exception ex) {
              getLog ().error ("Failed to convert " + aFile.getName () + " to XSLT!", ex);
            }
          }
        }
      }
  }
}
