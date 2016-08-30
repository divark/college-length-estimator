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
public class Course {
    private String courseName;
    private double courseUnits;
    private ArrayList<Course> preRequisites = new ArrayList<>();
    private String prerequisitesGUIWorkAround;
    private ArrayList<Course> concurrentCourses = new ArrayList<>();
    private String concurrentGUIWorkAround;
    private boolean summerCompatible;
    private String summerCompatibleGUIWorkAround;
    private int nextTermIndex;
    private boolean isNowUsed;

    public Course() {
        courseName = "";
        courseUnits = 0;
        preRequisites = new ArrayList<>();
        prerequisitesGUIWorkAround = "";
        concurrentCourses = new ArrayList<>();
        concurrentGUIWorkAround = "";
        summerCompatible = false;
        summerCompatibleGUIWorkAround = "";
        nextTermIndex = 0;
        isNowUsed = false;
    }
    
    /**
     * 
     * @param myCourseName
     * @param myCourseUnits
     * @throws Exception when myCourseUnits is less than or equal to zero.
     */
    public Course(String myCourseName, double myCourseUnits) throws Exception {
        if(myCourseName.trim().equals("")) {
            throw new Exception("A course must have a name.");
        }
        
        courseName = myCourseName.trim();
        
        if(myCourseUnits <= 0) {
            throw new Exception("A course cannot have 0 or less units.");
        } else {
            courseUnits = myCourseUnits;
        }
        preRequisites = new ArrayList<>();
        concurrentCourses = new ArrayList<>();
        summerCompatible = false;
        nextTermIndex = 0;
        isNowUsed = false;
    }
    
    @Override
    public String toString() {
        String myCourseContents = "";
        String mySeperator = "|";
        String osLineSeperator = System.getProperty("line.separator");
        
        myCourseContents += "Course Name:" + courseName;
        myCourseContents += mySeperator + "Course Units:" + courseUnits;
        
        myCourseContents += mySeperator + "Prerequisites:";
        String myPrerequisitesSeperator = ",";
        for(Course myPrerequisiteCourse: preRequisites) {
            myCourseContents += myPrerequisiteCourse.getCourseName() + 
                    myPrerequisitesSeperator;
        }
        
        myCourseContents += mySeperator + "Concurrents:";
        for(Course myConcurrentCourse : concurrentCourses) {
            myCourseContents += myConcurrentCourse.getCourseName() + 
                    myPrerequisitesSeperator;
        }
        
        myCourseContents += mySeperator + "Summer Compatible:" + summerCompatible
                + osLineSeperator;
        return myCourseContents;
    }
    
    /**
     * Resets the nextTermIndex value to 0 and sets isNowUsed to false.
     */
    public void usageReset() {
        nextTermIndex = 0;
        isNowUsed = false;
    }
    /**
     * @return the courseName
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @param courseName the courseName to set
     * @throws java.lang.Exception
     */
    public void setCourseName(String courseName) throws Exception {
        if(courseName.trim().equals(""))
        {
            throw new Exception("A course must have a name.");
        }
        this.courseName = courseName.trim();
    }

    /**
     * @return the courseUnits
     */
    public double getCourseUnits() {
        return courseUnits;
    }

    /**
     * @param courseUnits the courseUnits to set
     * @throws Exception when the course units are less than or equal to zero.
     */
    public void setCourseUnits(double courseUnits) throws Exception {
        if(courseUnits <= 0) {
            throw new Exception("A course cannot have 0 or less units.");
        } else {
            this.courseUnits = courseUnits;
        }
    }
    
    /**
     * 
     * @param myCourse
     * @throws Exception when course is already in the list, or the course tries
     * to make itself a prerequisite.
     */
    public void addPrerequisite(Course myCourse) throws Exception {
        if(isCurrentlyInPrerequisites(myCourse.getCourseName())) {
            throw new Exception(String.format("%s is currently in prerequisites."
                    , myCourse.getCourseName()));
        } else if(myCourse.getCourseName().toLowerCase().equals(courseName.toLowerCase())) {
            throw new Exception(String.format("%s cannot be its own prerequisite.",
                    myCourse.getCourseName()));
        } else if(isCurrentlyInConcurrents(myCourse.getCourseName())) {
            throw new Exception(String.format("%s cannot be a prerequisite and "
                    + "concurrent course at the same time.", myCourse.getCourseName()));
        } else if(myCourse.isCurrentlyInPrerequisites(courseName)) {
            throw new Exception(String.format("%s and %s cannot be prerequisites"
                    + " of each other.", courseName, myCourse.getCourseName()));
        }
        else {
            getPreRequisites().add(myCourse);
        }
    }
    
    public boolean isInPrerequisites(String myCourseName) {
        for(int i = 0; i < preRequisites.size(); i++) {
            if(preRequisites.get(i).getCourseName().equals(myCourseName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @param myCourse
     * @throws Exception when course is already in the list, or the course tries
     * to make itself a concurrent.
     */
    public void addConcurrent(Course myCourse) throws Exception {
        if(isCurrentlyInConcurrents(myCourse.getCourseName())) {
            throw new Exception(String.format("%s is currently in %s's concurrents."
                    , myCourse.getCourseName(), this.getCourseName()));
        } else if(myCourse.getCourseName().toLowerCase().equals(courseName.toLowerCase())) {
            throw new Exception(String.format("%s cannot be its own concurrent.",
                    myCourse.getCourseName()));
        } else if(isInPrerequisites(myCourse.getCourseName())) {
            throw new Exception(String.format("%s cannot be a prerequisite and "
                    + "concurrent course at the same time.", myCourse.getCourseName()));
        } else {
            getConcurrentCourses().add(myCourse);
            myCourse.getConcurrentCourses().add(this);
        }
    }
    
    /**
     * 
     * @param myCourseName
     * @return True if the course is found in the prerequisites list, otherwise returns
     * false.
     */
    public boolean isCurrentlyInPrerequisites(String myCourseName) {
        for(int i = 0; i < getPreRequisites().size(); i++) {
            Course myCurrentCourseInCollection = getPreRequisites().get(i);
            
            if(myCurrentCourseInCollection.getCourseName().toLowerCase().equals(myCourseName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @param myCourseName
     * @return True if the course is found in the concurrent list, otherwise returns
     * false.
     */
    public boolean isCurrentlyInConcurrents(String myCourseName) {
        for(int i = 0; i < getConcurrentCourses().size(); i++) {
            Course myCurrentCourseInCollection = getConcurrentCourses().get(i);
            
            if(myCurrentCourseInCollection.getCourseName().toLowerCase().equals(myCourseName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @param myCourseName
     * @throws Exception if a Course with myCourseName is not found.
     */
    public void removePrerequisiteByName(String myCourseName) throws Exception {
        for(int i = 0; i < getPreRequisites().size(); i++) {
            if(getPreRequisites().get(i).getCourseName().toLowerCase().equals(myCourseName.toLowerCase())) {
                getPreRequisites().remove(i);
                return;
            }
        }
        throw new Exception("Course not found: " + myCourseName);
    }
    
    /**
     * 
     * @param myCourseName
     * @throws Exception if a Course with myCourseName is not found.
     */
    public void removeConcurrentByName(String myCourseName) throws Exception {
        for(Course myConcurrentInMyCourse : concurrentCourses) {
            if(myConcurrentInMyCourse.getCourseName().equals(myCourseName)) {
                concurrentCourses.remove(myConcurrentInMyCourse);
                myConcurrentInMyCourse.getConcurrentCourses().remove(this);
                return;
            }
        }
        throw new Exception("Course not found: " + myCourseName);
    }

    /**
     * @return the summerCompatible
     */
    public boolean isSummerCompatible() {
        return summerCompatible;
    }

    /**
     * @param summerCompatible the summerCompatible to set
     */
    public void setSummerCompatible(boolean summerCompatible) {
        this.summerCompatible = summerCompatible;
    }

    /**
     * @return the preRequisites
     */
    public ArrayList<Course> getPreRequisites() {
        return preRequisites;
    }

    /**
     * @return the nextTermIndex
     */
    public int getNextTermIndex() {
        return nextTermIndex;
    }
    
    /**
     * Adds one to the next term index of the current course.
     */
    public void incrementNextTermIndex() {
        nextTermIndex++;
    }

    /**
     * @return the isNowUsed
     */
    public boolean isIsNowUsed() {
        return isNowUsed;
    }

    /**
     * @param isNowUsed the isNowUsed to set
     */
    public void setIsNowUsed(boolean isNowUsed) {
        this.isNowUsed = isNowUsed;
    }

    /**
     * @return the concurrentCourses
     */
    public ArrayList<Course> getConcurrentCourses() {
        return concurrentCourses;
    }

    /**
     * @return the prerequisitesGUIWorkAround
     */
    public String getPrerequisitesGUIWorkAround() {
        return prerequisitesGUIWorkAround;
    }

    /**
     * @param prerequisitesGUIWorkAround the prerequisitesGUIWorkAround to set
     */
    public void setPrerequisitesGUIWorkAround(String prerequisitesGUIWorkAround) {
        this.prerequisitesGUIWorkAround = prerequisitesGUIWorkAround;
    }

    /**
     * @return the concurrentGUIWorkAround
     */
    public String getConcurrentGUIWorkAround() {
        return concurrentGUIWorkAround;
    }

    /**
     * @param concurrentGUIWorkAround the concurrentGUIWorkAround to set
     */
    public void setConcurrentGUIWorkAround(String concurrentGUIWorkAround) {
        this.concurrentGUIWorkAround = concurrentGUIWorkAround;
    }

    /**
     * @return the summerCompatibleGUIWorkAround
     */
    public String getSummerCompatibleGUIWorkAround() {
        return summerCompatibleGUIWorkAround;
    }

    /**
     * @param summerCompatibleGUIWorkAround the summerCompatibleGUIWorkAround to set
     */
    public void setSummerCompatibleGUIWorkAround(String summerCompatibleGUIWorkAround) {
        this.summerCompatibleGUIWorkAround = summerCompatibleGUIWorkAround;
    }
}
