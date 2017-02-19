/* 
 *  Copyright (C) 2006 Aleksi Ahtiainen, Mikko Rahikainen.
 * 
 *  This file is part of Plaggie.
 *
 *  Plaggie is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2 of the License,
 *  or (at your option) any later version.
 *
 *  Plaggie is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaggie; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301  USA
 */
package plag.parser.report;

import plag.parser.*;

import java.util.List;

/**
 * A report generator, which generates a report of a set or a single Detection result.
 *
 */
public interface ReportGenerator
{
    /**
     * Generates a report of the given detection result.
     */
    public void generateReport(DetectionResult detectionResult)
	throws Exception;

    /**
     * Generates a report of the DetectionResults stored in the given list.
     */
    public void generateReport(List detectionResults)
	throws Exception;
}
