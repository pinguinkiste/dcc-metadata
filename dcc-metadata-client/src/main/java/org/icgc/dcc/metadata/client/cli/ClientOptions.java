/*
 * Copyright (c) 2015 The Ontario Institute for Cancer Research. All rights reserved.                             
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.metadata.client.cli;

import java.io.File;

import lombok.ToString;

import com.beust.jcommander.Parameter;

@ToString
public class ClientOptions {

  /**
   * Input
   */
  @Parameter(names = { "-i", "--input-dir" }, help = true, description = "The input directory")
  public File inputDir = new File(".");

  /**
   * Output
   */
  @Parameter(names = { "-o", "--output-dir" }, help = true, description = "The output directory")
  public File outputDir = new File(".");
  @Parameter(names = { "-m", "--manifest-filename" }, help = true, description = "The manifest file name to be created in the output directory")
  public String manifestFileName = "manifest.txt";

  /**
   * Info
   */
  @Parameter(names = { "-v", "--version" }, help = true, description = "Show version information")
  public boolean version;
  @Parameter(names = { "-h", "--help" }, help = true, description = "Show help information")
  public boolean help;

}
