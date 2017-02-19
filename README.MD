README for Plaggie

Program Code Plagiarism Detection Tool
plag.parser.plaggie.Plaggie

------------------------------------------------------------------------
1. License information
------------------------------------------------------------------------

Copyright (C) 2006 Aleksi Ahtiainen and Mikko Rahikainen.

See COPYING.PLAGGIE for Plaggie license.
See COPYING.CUP for license on the CUP generated parts.
See COPYING.JAVA15GRAMMAR for the license of the Java 1.5 grammar that
was used in this software.

........................................................................
Copyright notice for Plaggie:

    This program (Plaggie) is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License as published
    by the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
    02110-1301  USA

........................................................................
Copyright notice for CUP Parser Generator:

CUP Parser Generator Copyright Notice, License, and Disclaimer
Copyright 1996-1999 by Scott Hudson, Frank Flannery, C. Scott Ananian

Permission to use, copy, modify, and distribute this software and its
documentation for any purpose and without fee is hereby granted,
provided that the above copyright notice appear in all copies and that
both the copyright notice and this permission notice and warranty
disclaimer appear in supporting documentation, and that the names of
the authors or their employers not be used in advertising or publicity
pertaining to distribution of the software without specific, written
prior permission.

The authors and their employers disclaim all warranties with regard to
this software, including all implied warranties of merchantability and
fitness. In no event shall the authors or their employers be liable
for any special, indirect or consequential damages or any damages
whatsoever resulting from loss of use, data or profits, whether in an
action of contract, negligence or other tortious action, arising out
of or in connection with the use or performance of this software.
This is an open source license. It is also GPL-Compatible (see entry
for "Standard ML of New Jersey"). The portions of CUP output which are
hard-coded into the CUP source code are (naturally) covered by this
same license, as is the CUP runtime code linked with the generated
parser.

Java is a trademark of Sun Microsystems, Inc. References to the Java
programming language in relation to JLex are not meant to imply that
Sun endorses this product. 

........................................................................
Copyright notice for Java 1.5. Grammar:

/* Java 1.5 parser for CUP.
 * Based on a Java 1.5 pre-release version by C. Scott Ananian.
 * Extended to support annotations by Jeremy Brown.  Original version
 * copyright (C) C. Scott Ananian, 2003; modifications copyright (C) 
 * Jeremy H. Brown, 2006.  This program is released under the terms
 * of the GPL; see the file COPYING for more details.  There is NO
 * WARRANTY on this code.  
 */

------------------------------------------------------------------------
2. Introduction
------------------------------------------------------------------------

This tool was created to address the problem of program code
plagiarism at the Helsinki University of Technology. The tool is a
stand-alone command line Java application and can be used to check
programming assignment submissions for possible plagiarism.

It is important to know that this is just a tool and the reported
possible plagiarism cases still need to be checked by hand before
taking further action.

------------------------------------------------------------------------
3. Current list of features
------------------------------------------------------------------------

The current features of the tool are listed here. See the section 12
(Further Development) for information on suggesting new features.

- Programming languages supported: Java 1.5
- Configuration via a configuration file
- Command line interface for running the program
- Detection reports generated in graphical HTML format (using frames)
- Exclusion of code from the detection according to the following
criteria:
   * filename
   * subdirectory name
   * interface
- Exclusion of template code (i.e. code distributed as a part of the
assignment to the students) from the detection
- Blacklist file for highlighting results of certain students

------------------------------------------------------------------------
4. Algorithm used
------------------------------------------------------------------------

The basic algorithm used for comparing two source code files is
tokenization followed by greedy string tiling. This algorithm is
documented in [1]. The algorithm in the current implementation is not
optimized with the optimizations documented also in [1].

The algorithm has been extended to support the exclusion of existing
code (e.g. code given within the programming assignment description)
from the comparison.

Two similarity values (similarity values A and B, corresponding to two
submissions A and B) are counted both between two files and two
submissions, which can consist of several files. The file similarity
value is the percentage of matched tokens compared to the total number
of tokens within the file. The submission similarity value A is
calculated by taking the best match (i.e. the highest file similarity
value A) of each file in submission A and counting the average of
these values. The submission similarity value B is the average of the
corresponding similarity values in submission B.

------------------------------------------------------------------------
5. Installation
------------------------------------------------------------------------

* Prerequisites and external software
  - Java v. 1.3.1
  - make
  - JavaCUP v. 0.10j (Provided as a tar.gz with the package,
  originally from http://www.cs.princeton.edu/~appel/modern/java/CUP/)
  - Java 1.5 Grammar & Lexer for JavaCUP (Originally from 
  http://people.csail.mit.edu/jhbrown/javagrammar/index.html, altered
  version provided within the software package)

* Extraction of the detection tool (plaggie.tar.gz)

  - Create an installation directory for the plagiarism tool
  - Extract the package in it
  - Make sure that the installation directory is in your CLASSPATH

* Installation of the prerequisite software and packages

  - if JavaCUP is not installed:
     - Create an installation directory for the JavaCUP files and
     extract the package in it
     - Make sure that the JavaCUP directory is in your CLASSPATH
     - Compile the JavaCUP files in the installation directory:
         javac java_cup/*.java java_cup/runtime/*.java

* Installation of the plagiarism detection tool

  - The detection tool is provided as Java source files, which need
    to be compiled in the installation directory:
    * First, edit Makefile and set variables
        CUP_PATH  and
        PLAGGIE_PATH
      to point to the locations of CUP and Plaggie directories.
    * Then run:
        make all

* Documentation

  - Documentation is provided in this README file. Additional
    configuration documentation is provided in the configuration file
    template, plaggie.properties.

------------------------------------------------------------------------
6. Configuration of the detection tool
------------------------------------------------------------------------

Configuration parameters are documented within the configuration file
(plaggie.properties). This file needs to exist in the directory
where the plagiarism detection is executed. Before configuring and
running the tool, the following issues need to be known about the
programming exercise to be checked:

- Are the students using some common code base or templates
  distributed along with the assignment?
  If so, then consider the following:
  - Those files not modified by the students should be added to the
  configuration parameter
  plag.parser.plaggie.excludeFiles
  - If the students are adding their code within these files, the base
  files have to be added to the configuration parameter
  plag.parser.plaggie.excludeCodeFiles.

- If certain subdirectories within the submissions are not to be
  checked (e.g. if entire packages are distributed), they can be added
  to the 
  plag.parser.plaggie.excludeSubdirectories 
  configuration parameter.

------------------------------------------------------------------------
7. Running the detection tool
------------------------------------------------------------------------

The tool is executed from the command line:

   % java plag.parser.plaggie.Plaggie 
       [-s<number>] [-nohtml] directory1|file1 [directory2|file2]

At least one directory containing the submissions is required. If two
directories or files are specified, they are assumed to be submissions
of two students and are compared to each other.

Optional parameters:

  -s<number> A new value for the minimum submission similarity
             value. Overrides the value specified in the configuration
             file.
  -nohtml    No HTML report generated, a textual report is always
             generated on the standard output

During execution the tool displays statistics of its progress every 5
second. After the detection has process has completed, the tool generates a
report of the detected possible plagiarism cases. The format of the
report can be specified in the configuration file; usually it is a set
of HTML pages.

------------------------------------------------------------------------
8. The detection report
------------------------------------------------------------------------

* Viewing the report

The report can be viewed by any HTML browser, which supports frames.

* Contents of the report

** Configuration

Information about the main properties of the configuration are
included in the report.

** Statistics

Several statistics are generated of all the results (even the ones not
included in the report separately). Distributions of the different
similarity values are included. The different kinds of similarity
values are documented below.

Also a distribution of the number of files in submissions is reported
as are the final values of the counters.

** Report

Top results are reported as separate set of HTML pages. The results
can be sorted according to different similarity values. The similarity
values are:

 - Submission similarity
     Product of values A and B. High value in this is the best
     indicator of possible plagiarism case.
 - Submission similarity A and B
     Indicates the percentage of code in one submission, that was also
   found in the other submission.
 - Maximum file similarity
     The maximum similarity between a pair of source files in the two
     submissions.

By clicking the student identification numbers, an extensive report of
the corresponding similarity comparison between two students is
opened. In that report, the following similarity values between the
students' files are shown:

 - File similarity A and B
     Indicates the percentage of tokens matched in the identical token
   sequences between the two files.

------------------------------------------------------------------------
9. Known problems
------------------------------------------------------------------------

The detection algorithm is not at its best when using it on GUI code
or some code generated using automated development tools. The problem
with GUI code is, that it is common to have many (member) variable
declarations or method calls in one section. In that case the
algorithm might detect a match even if the variables and called
methods are completely different.

------------------------------------------------------------------------
10. Known unsuccessful attacks
------------------------------------------------------------------------

The following commonly used attacks are not usable againts this
plagiarism detector, since the detection is based completely on
structural analysis of the code:

* Changing the comments

* Changing the intendation

* Method and variable name changes

The following attack does not work, because the files are compared to
each other without taking the file names into account:

* Renaming the classes

------------------------------------------------------------------------
11. Known successful attacks
------------------------------------------------------------------------

Because the detection algorithm is based on finding similar token
sequences of some minimum length, an attack can be made by altering
the program code so much, that the similar sequences are shorter than
the configured minimum match length. The minimum match length can be
configured to be shorter, but then the possiblity for false matches
increases.

The following attacks can be successful against the algorithm:

* Moving inline code to separate methods and vice versa

* Inclusion of redundant program code

* Changing the order of if-else blocks and case-blocks

------------------------------------------------------------------------
12. Further development
------------------------------------------------------------------------

If you have ideas about further development of the tool, please
contact the author, Aleksi Ahtiainen, at Aleksi.Ahtiainen@iki.fi.

------------------------------------------------------------------------
14. List of references
------------------------------------------------------------------------

[1] Prechelt Lutz, Malpohl Guido, Phlippsen Michael: JPlag: Finding
plagiarisms among a set of programs, Fakultät für Informatik,
Universität Karlsruhe. 28.3.2000,
http://wwwipd.ira.uka.de/~prechelt/Biblio/jplagTR.pdf
