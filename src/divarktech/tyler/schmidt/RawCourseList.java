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
public class RawCourseList {
    private ArrayList<Course> myRawCourseList = new ArrayList<>();
    
    /**
     * 
     * @param myCourse
     * @throws Exception if myCourse is already in the myRawCourseList List.
     */
    public void addToList(Course myCourse) throws Exception { 
        for(int i = 0; i < getMyRawCourseList().size(); i++) {
            Course courseInCollection = getMyRawCourseList().get(i);
            
            if(courseInCollection.getCourseName().toLowerCase().equals(myCourse.getCourseName().toLowerCase())) {
                throw new Exception("There already exists a version of " + myCourse.getCourseName());
            }
        }
        getMyRawCourseList().add(myCourse);
    }
    
    /**
     * 
     * @param myCourseName
     * @return Course object found by myCourseName.
     * @throws Exception if Course object was not found by myCourseName.
     */
    public Course getCourseByName(String myCourseName) throws Exception {
        for(int i = 0; i < myRawCourseList.size(); i++) {
            Course currentCourse = myRawCourseList.get(i);
            
            if(currentCourse.getCourseName().toLowerCase().equals(myCourseName.toLowerCase())) {
                return currentCourse;
            }
        }
        throw new Exception("Course not found: " + myCourseName);
    }
    
    /**
     * 
     * @param myCourseName
     * @throws Exception if Course object was not found by myCourseName.
     */
    public void removeCourseByName(String myCourseName) throws Exception {
        boolean wasFound = false;
        
        for(int i = 0; i < myRawCourseList.size(); i++) {
            if(myRawCourseList.get(i).getCourseName().toLowerCase().equals(myCourseName.toLowerCase())) {
                myRawCourseList.remove(i);
                wasFound = true;
                break;
            }
        }
        
        for(int i = 0; i < myRawCourseList.size(); i++) {
            try {
                myRawCourseList.get(i).removeConcurrentByName(myCourseName);
            } catch(Exception courseNotFound) {
                //Do nothing.
            }
            
            try {
                myRawCourseList.get(i).removePrerequisiteByName(myCourseName);
            } catch(Exception courseNotFound) {
                //Do nothing.
            }
        }
        
        if(!wasFound) {
            throw new Exception("Course not found: " + myCourseName);
        } else {
            System.gc();
        }
    }

    /**
     * @return the myRawCourseList
     */
    public ArrayList<Course> getMyRawCourseList() {
        return myRawCourseList;
    }
    
    /**
     * 
     * @return Formatted string of all courses in myRawCourseList with the 
     * course name and course units.
     */
    public String printListOfCourses() {
        String courseInformationToBeSent = "";
        
        for(int i = 0; i < myRawCourseList.size(); i++) {
            courseInformationToBeSent += String.format("%s: %s Units\n", 
                    myRawCourseList.get(i).getCourseName(), 
                    myRawCourseList.get(i).getCourseUnits());
        }
        return courseInformationToBeSent;
    }
}
