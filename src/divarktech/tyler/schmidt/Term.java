/*
Copyright (C) 2016  Tyler Schmidt, Jesse Paone

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program in a file called LICENSE.  
If not, see <http://www.gnu.org/licenses/>
*/
package divarktech.tyler.schmidt;

import java.util.ArrayList;

/**
 *
 * @author divark
 */
public class Term {
    private ArrayList<Course> myCourses = new ArrayList<>();
    private double termLimit;
    private double termCount;
    private boolean isSummerTerm;
    
    public Term() {
        myCourses = new ArrayList<>();
        termLimit = 0;
        termCount = 0;
        isSummerTerm = false;
    }
    
    /**
     * 
     * @param currentCourse
     * @throws Exception when currentCourse is already in this term, or it does
     * not fit based on the termLimit or termCount in comparison to the termLimit.
     */
    public void addToTerm(Course currentCourse) throws Exception {
        if(isCurrentlyInTerm(currentCourse.getCourseName())) {
            throw new Exception(String.format("%s is already in the term.", 
                    currentCourse.getCourseName()));
        } else if(currentCourse.getCourseUnits() + getTermCount() <= getTermLimit()) {
            myCourses.add(currentCourse);
            termCount += currentCourse.getCourseUnits();
        } else {
            throw new Exception(String.format("%s does not fit in term. (%s + "
                    + "%s exceeds %s)", currentCourse.getCourseName(), 
                    currentCourse.getCourseUnits(), getTermCount(), getTermLimit()));
        }
    }
    
    public ArrayList<Course> getMyCourses() {
        return myCourses;
    }
    
    /**
     * 
     * @param myCourseName
     * @return True if myCourseName is found in the current term, otherwise
     * returns false.
     */
    public boolean isCurrentlyInTerm(String myCourseName) {
        for(int i = 0; i < myCourses.size(); i++) {
            Course myCurrentCourseInCollection = myCourses.get(i);
            
            if(myCurrentCourseInCollection.getCourseName().toLowerCase().equals(myCourseName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @return Formatted string of all courses with course name and course units.
     */
    public String printCourses() {
        String courseInformationToBeReturned = "";
        
        for(int i = 0; i < myCourses.size(); i++) {
            Course myCurrentCourse = myCourses.get(i);
            String currentCourseInformation = myCurrentCourse.getCourseName() + ": " + myCurrentCourse.getCourseUnits() + " Units\n";
            
            courseInformationToBeReturned += currentCourseInformation;
        }
        return courseInformationToBeReturned;
    }

    /**
     * @return the termLimit
     */
    public double getTermLimit() {
        return termLimit;
    }

    /**
     * @param termLimit the termLimit to set
     */
    public void setTermLimit(double termLimit) throws Exception {
        if(termLimit <= 0) {
            throw new Exception("Term Limit cannot be 0 or negative.");
        }
        this.termLimit = termLimit;
    }

    /**
     * @return the termCount
     */
    public double getTermCount() {
        return termCount;
    }

    /**
     * @return the isSummerTerm
     */
    public boolean isIsSummerTerm() {
        return isSummerTerm;
    }

    /**
     * @param isSummerTerm the isSummerTerm to set
     */
    public void setIsSummerTerm(boolean isSummerTerm) {
        this.isSummerTerm = isSummerTerm;
    }
}
