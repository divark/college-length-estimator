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
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author divark
 */
public class CLEImplementation {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String myTermName = "";
        double myTermLimit = 0.0;
        double mySummerTermLimit = 0.0;
        int amountOfTermsInYearExcludingSummer = 0;
        RawCourseList rawCourseList = new RawCourseList();
        Scanner kbd = new Scanner(System.in);
        
        System.out.print("Enter Term Name: ");
        myTermName = kbd.nextLine();
        
        myTermLimit = getTermLimit();
        
        askForCoursesFromUser(rawCourseList);
        
        if(hasSummerCourses(rawCourseList.getMyRawCourseList())) {
            mySummerTermLimit = getTermLimit();
            amountOfTermsInYearExcludingSummer = askForAmountOfTermsInYear();
        }
        
        System.out.println("\nHere's the raw list: ");
        System.out.println(rawCourseList.printListOfCourses());
        
        askForConcurrentCoursesFromUser(rawCourseList);
        askForPrerequisitesFromUser(rawCourseList);
        
        String myRequestToRepeat = "";
        ArrayList<Term> myTermList = new ArrayList<>();
        do {
            try {
                myTermList = processTerms(myTermLimit, mySummerTermLimit, amountOfTermsInYearExcludingSummer, rawCourseList);
            } catch(Exception interpretiveError) {
                System.out.println(interpretiveError.getMessage());
                System.exit(1);
            }

            System.out.println(printTerms(myTermList, myTermName, amountOfTermsInYearExcludingSummer, myTermLimit, mySummerTermLimit));
            System.out.print("Type y and press enter if you'd like to repeat this process: ");
            myRequestToRepeat = kbd.nextLine();
            
            if(myRequestToRepeat.toLowerCase().equals("y")) {
                System.out.println("Current Term Limit: " + myTermLimit);
                System.out.print("Enter a new term limit if desired (Invalid entry will keep existing): ");
                
                try {
                    myTermLimit = Double.parseDouble(kbd.nextLine());
                } catch(NumberFormatException notAValidEntry) {
                    //Proceed unchanged
                }
            }
            
            for(int i = 0; i < rawCourseList.getMyRawCourseList().size(); i++) {
                rawCourseList.getMyRawCourseList().get(i).usageReset();
            }
        } while(myRequestToRepeat.toLowerCase().equals("y"));
    }
    
    public static void askForCoursesFromUser(RawCourseList rawCourseList) {
        String myCourseName = "";
        double myCourseUnits = 0.0;
        String mySummerConfirmation = "";
        boolean isInErrorState = false;
        Scanner kbd = new Scanner(System.in);
        
        while(true) {            
            if(isInErrorState) {
                System.out.print(String.format("%s already entered. Try again: ", myCourseName));
                isInErrorState = false;
            } else {
                System.out.print("Enter Course Name (Type q to quit entering courses): ");
            }
            myCourseName = kbd.nextLine();
            
            if(myCourseName.toLowerCase().equals("q")) {
                break;
            }
            
            do {
                if(isInErrorState) {
                    System.out.print("Invalid Course Units. Try again: ");
                } else {
                    System.out.print(String.format("Enter Units for %s: ", myCourseName));
                }
                
                try {
                    myCourseUnits = Double.parseDouble(kbd.nextLine());
                    
                    if(myCourseUnits <= 0) {
                        isInErrorState = true;
                    } else {
                        isInErrorState = false;
                    }
                } catch(NumberFormatException invalidCourseUnits) {
                    isInErrorState = true;
                }
            } while(isInErrorState);
            
            System.out.print("If this course is summer compatible (and you want "
                    + "to process summer terms), type y and press enter: ");
            mySummerConfirmation = kbd.nextLine();
            
            try {
                Course myCourse = new Course(myCourseName, myCourseUnits);
                if(mySummerConfirmation.toLowerCase().equals("y")) {
                    myCourse.setSummerCompatible(true);
                }
                rawCourseList.addToList(myCourse);
            } catch(Exception duplicateFound) {
                isInErrorState = true;
            }
        }
    }
    
    public static void askForPrerequisitesFromUser(RawCourseList rawCourseList) {
        String nameOfCourseToBeFound = "";
        Scanner kbd = new Scanner(System.in);
        boolean isInErrorState = false;
        Course myCourseThatWasFound = null;
        
        while(true) {
            do {
                if(isInErrorState) {
                    System.out.print("Course not found. Try again: (q to quit)");
                    isInErrorState = false;
                } else {
                    System.out.print("Enter a course with a prerequisite (q to quit or none): ");
                }

                nameOfCourseToBeFound = kbd.nextLine();

                if(nameOfCourseToBeFound.toLowerCase().equals("q")) {
                    break;
                }

                try {
                    myCourseThatWasFound = rawCourseList.getCourseByName(nameOfCourseToBeFound);
                } catch(Exception courseNotFound) {
                    isInErrorState = true;
                }
            } while(isInErrorState);

            if(nameOfCourseToBeFound.toLowerCase().equals("q")) {
                break;
            }

            do {
                if(isInErrorState) {
                    System.out.print("Invalid or not found. Try again (q to quit): ");
                    isInErrorState = false;
                } else {
                    System.out.print(String.format("Enter the prerequisite for %s "
                            + "(q to quit or none): ", myCourseThatWasFound.getCourseName()));
                }

                nameOfCourseToBeFound = kbd.nextLine();

                if(nameOfCourseToBeFound.toLowerCase().equals("q")) {
                    break;
                }

                try {
                    myCourseThatWasFound.addPrerequisite(rawCourseList.getCourseByName(nameOfCourseToBeFound));
                    System.out.println(String.format("%s was added to %s's prerequisites.", nameOfCourseToBeFound, myCourseThatWasFound.getCourseName()));
                } catch(Exception courseNotFound) {
                    isInErrorState = true;
                }
            } while(isInErrorState || !nameOfCourseToBeFound.toLowerCase().equals("q"));
        }
    }
    
    public static void askForConcurrentCoursesFromUser(RawCourseList rawCourseList) {
        String nameOfCourseToBeFound = "";
        Scanner kbd = new Scanner(System.in);
        boolean isInErrorState = false;
        Course myCourseThatWasFound = null;
        
        while(true) {
            do {
                if(isInErrorState) {
                    System.out.print("Course not found. Try again: (q to quit)");
                    isInErrorState = false;
                } else {
                    System.out.print("Enter a course with a concurrent requirement (q to quit or none): ");
                }

                nameOfCourseToBeFound = kbd.nextLine();

                if(nameOfCourseToBeFound.toLowerCase().equals("q")) {
                    break;
                }

                try {
                    myCourseThatWasFound = rawCourseList.getCourseByName(nameOfCourseToBeFound);
                } catch(Exception courseNotFound) {
                    isInErrorState = true;
                }
            } while(isInErrorState);

            if(nameOfCourseToBeFound.toLowerCase().equals("q")) {
                break;
            }

            do {
                if(isInErrorState) {
                    System.out.print("Invalid or not found. Try again (q to quit): ");
                    isInErrorState = false;
                } else {
                    System.out.print(String.format("Enter the concurrent requirement for %s "
                            + "(q to quit or none): ", myCourseThatWasFound.getCourseName()));
                }

                nameOfCourseToBeFound = kbd.nextLine();

                if(nameOfCourseToBeFound.toLowerCase().equals("q")) {
                    break;
                }

                try {
                    myCourseThatWasFound.addConcurrent(rawCourseList.getCourseByName(nameOfCourseToBeFound));
                    rawCourseList.getCourseByName(nameOfCourseToBeFound).addConcurrent(myCourseThatWasFound);
                    System.out.println(String.format("%s was added to %s's concurrents.", nameOfCourseToBeFound, myCourseThatWasFound.getCourseName()));
                } catch(Exception courseNotFound) {
                    isInErrorState = true;
                }
            } while(isInErrorState || !nameOfCourseToBeFound.toLowerCase().equals("q"));
        }
    }
    
    public static String printTerms(ArrayList<Term> myTermList, String myTermName,
            int myAmountOfTermsInYear, double myTermLimit, double mySummerTermLimit) {
        String myTermResults = "";
        int mySummerTermCount = 1;
        int myGeneralTermCount = 1;
        String osLineSeperator = System.getProperty("line.separator");
        
        for(Term myCurrentTerm : myTermList) {
            if(myCurrentTerm.isIsSummerTerm()) {
                myTermResults += "Summer " + myTermName + " " + mySummerTermCount + osLineSeperator;
                mySummerTermCount++;
            } else {
                myTermResults += myTermName + " " + myGeneralTermCount + osLineSeperator;
                myGeneralTermCount++;
            }
            myTermResults += myCurrentTerm.printCourses();
            myTermResults += "Total Units: " + myCurrentTerm.getTermCount() + osLineSeperator + osLineSeperator;
        }
        return myTermResults;
    }
    
    public static ArrayList<Term> processTerms(double myTermLimit,
            double mySummerTermLimit, int myAmountOfTermsInYear,
            RawCourseList myRawCourseList) throws Exception {
        ArrayList<Term> myTermList = new ArrayList<>();
        int myTermIndex = 0;
        int yearCount = 1;
        long mySeed = System.nanoTime();
        RawCourseList myShuffledListOfRawCourses = new RawCourseList();
        myShuffledListOfRawCourses.getMyRawCourseList().addAll(myRawCourseList.getMyRawCourseList());
        Collections.shuffle(myShuffledListOfRawCourses.getMyRawCourseList(), new Random(mySeed));
        
        while(!hasFullyUsedCourses(myShuffledListOfRawCourses.getMyRawCourseList())) {
            if(myTermIndex != 0 && myTermIndex % myAmountOfTermsInYear == 0) {
                yearCount++;
            }
            Term myCurrentTerm = new Term();
            
            try {
                if(myTermIndex != 0 && myAmountOfTermsInYear != 0 && myTermIndex % myAmountOfTermsInYear == 0 && mySummerTermLimit != 0) {
                    myCurrentTerm.setTermLimit(mySummerTermLimit);
                    myCurrentTerm.setIsSummerTerm(true);
                } else {
                    myCurrentTerm.setTermLimit(myTermLimit);
                    myCurrentTerm.setIsSummerTerm(false);
                }
            } catch(Exception invalidTermLimit) {
                throw new Exception(invalidTermLimit.getMessage());
            }

            courseSearch:
            for(int i = 0; i < myShuffledListOfRawCourses.getMyRawCourseList().size(); i++) {
                Course myCurrentCourse = myShuffledListOfRawCourses.getMyRawCourseList().get(i);
                
                if(!myCurrentCourse.getConcurrentCourses().isEmpty()) {
                    myCurrentCourse = concurrentProcessing(myCurrentCourse, 
                            myCurrentTerm, myTermLimit, mySummerTermLimit, 
                            myTermIndex, myAmountOfTermsInYear, yearCount);
                } else if(!myCurrentCourse.getPreRequisites().isEmpty()) {
                    myCurrentCourse = 
                    getCurrentCoursePrerequisite(myShuffledListOfRawCourses.getMyRawCourseList().get(i),
                            myCurrentTerm, myTermIndex, myTermLimit, mySummerTermLimit,
                            myAmountOfTermsInYear, yearCount);
                }
                
                if(myCurrentCourse.getNextTermIndex() == myTermIndex && !myCurrentCourse.isIsNowUsed()) {
                    try {
                        if(myCurrentTerm.isIsSummerTerm() && !myCurrentCourse.isSummerCompatible()) {
                            if(myCurrentCourse.getCourseUnits() > myTermLimit) {
                                throw new Exception(String.format("%s violates the Term Limit.", myCurrentCourse.getCourseName()));
                            } else {
                                myCurrentCourse.incrementNextTermIndex();
                            }
                        } else {
                            int termExclusiveInflation = 0;
                            if(yearCount > 1) {
                                if(mySummerTermLimit != 0) {
                                    termExclusiveInflation = (yearCount - 1) * (myAmountOfTermsInYear + 1);
                                } else {
                                    termExclusiveInflation = (yearCount - 1) * myAmountOfTermsInYear;
                                }
                            }
                            if(!myCurrentCourse.getTermExclusiveIdentifiers().isEmpty()) {
                                for(Integer myCurrentCourseTermExclusive : myCurrentCourse.getTermExclusiveIdentifiers()) {
                                    if((myCurrentCourseTermExclusive + termExclusiveInflation) % (myTermIndex + 1) == 0) {
                                        myCurrentTerm.addToTerm(myCurrentCourse);
                                        myCurrentCourse.setIsNowUsed(true);
                                        i = -1;
                                        continue courseSearch;
                                    }
                                }
                                myCurrentCourse.incrementNextTermIndex();
                            } else {
                                myCurrentTerm.addToTerm(myCurrentCourse);
                                myCurrentCourse.setIsNowUsed(true);
                            }
                        }
                    } catch(Exception courseWontFitInTerm) {
                        if(myCurrentCourse.getCourseUnits() > myTermLimit && myCurrentCourse.getCourseUnits() > mySummerTermLimit) {
                            throw new Exception(String.format("%s violates the Term Limits.", myCurrentCourse.getCourseName()));
                        } else if(!myCurrentTerm.isIsSummerTerm() && 
                                !myCurrentCourse.isSummerCompatible() &&
                                myCurrentCourse.getCourseUnits() > myTermLimit) {
                            throw new Exception(String.format("%s violates the Term Limits.", myCurrentCourse.getCourseName()));
                        } else {
                            myCurrentCourse.incrementNextTermIndex();
                        }
                    }
                }
            }
            //Just in case a summer term is empty.
            if(!(myCurrentTerm.getMyCourses().isEmpty() && myCurrentTerm.isIsSummerTerm())) {
                myTermList.add(myCurrentTerm);
            }
            myTermIndex++;
        }
        return myTermList;
    }
    
    public static Course concurrentProcessing(Course myCourse, Term myCurrentTerm, 
            double myTermLimit, double mySummerTermLimit, int myTermIndex,
            int myAmountOfTerms, int myYearCount) throws Exception {
        if(myCourse.isIsNowUsed() || myCourse.getNextTermIndex() != myTermIndex) {
            return myCourse;
        }
        ArrayList<Course> myConcurrentChain = new ArrayList<>();
       
        myConcurrentChain.add(myCourse);
       
        //Implementation of identifying chain modified by []----[]:
        for(int i = 0; i < myConcurrentChain.size(); i++) {
            Course myCourseInConcurrentChain = myConcurrentChain.get(i);
            
            for(Course myConcurrentCourse : myCourseInConcurrentChain.getConcurrentCourses()) {
                if(!myConcurrentChain.contains(myConcurrentCourse)) {
                    myConcurrentChain.add(myConcurrentCourse);
                }
            }
        }
        
        double myConcurrentChainUnits = 0.0;
        for(Course courseInConcurrentChain : myConcurrentChain) {
            myConcurrentChainUnits += courseInConcurrentChain.getCourseUnits();
        }
        
        if(myConcurrentChainUnits > myTermLimit && myConcurrentChainUnits > mySummerTermLimit) {
            throw new Exception(String.format("A concurrent chain with %s in"
                    + " it violates the term limits given.", myCourse.getCourseName()));
        }
        
        for(Course courseInConcurrentChain : myConcurrentChain) {
            if(!courseInConcurrentChain.getPreRequisites().isEmpty() && 
                    !hasFullyUsedCourses(courseInConcurrentChain.getPreRequisites())) {
                for(Course myConcurrentCourse : myConcurrentChain) {
                    myConcurrentCourse.incrementNextTermIndex();
                }
                return getCurrentCoursePrerequisite(courseInConcurrentChain, 
                        myCurrentTerm, myTermIndex, myTermLimit, mySummerTermLimit,
                        myAmountOfTerms, myYearCount);
            } else if(courseInConcurrentChain.getNextTermIndex() != myCourse.getNextTermIndex()) {
                myCourse.incrementNextTermIndex();
                return myCourse;
            } else if(myCurrentTerm.getMyCourses().containsAll(courseInConcurrentChain.getPreRequisites())
                    && !courseInConcurrentChain.getPreRequisites().isEmpty()) {
                myCourse.incrementNextTermIndex();
                return myCourse;
            }
        }
        
        if(!myCourse.getTermExclusiveIdentifiers().isEmpty()) {
            ArrayList<Integer> termExclusivesThatWork = new ArrayList<>();
            
            for(Integer myTermExclusive : myCourse.getTermExclusiveIdentifiers()) {
                for(Course courseInConcurrentChain : myConcurrentChain) {
                    if(!courseInConcurrentChain.getTermExclusiveIdentifiers().isEmpty()
                            && !courseInConcurrentChain.getTermExclusiveIdentifiers()
                                    .contains(myTermExclusive)) {
                        break;
                    }
                }
                termExclusivesThatWork.add(myTermExclusive);
            }
            
            if(termExclusivesThatWork.isEmpty()) {
                throw new Exception(myCourse.getCourseName() + "'s concurrent chain"
                        + " has an impossible term exclusive pattern.");
            }
            
            int termExclusiveInflation = 0;
            if(myYearCount > 1) {
                if(mySummerTermLimit != 0) {
                    termExclusiveInflation = (myYearCount - 1) * (myAmountOfTerms + 1);
                } else {
                    termExclusiveInflation = (myYearCount - 1) * myAmountOfTerms;
                }
            }
            
            //Check to see if what works matches what term we're looking at
            //right now.
            boolean isInRightTerm = false;
            for(Integer myTermExclusiveThatWorks : termExclusivesThatWork) {
                if((myTermExclusiveThatWorks + termExclusiveInflation) % (myTermIndex + 1) == 0) {
                    isInRightTerm = true;
                    break;
                }
            }
            
            if(!isInRightTerm) {
                for(Course courseInConcurrentChain : myConcurrentChain) {
                    courseInConcurrentChain.incrementNextTermIndex();
                }
                return myCourse;
            }
        }
        
        if(myConcurrentChainUnits + myCurrentTerm.getTermCount() <= myCurrentTerm.getTermLimit()) {
            for(Course courseInConcurrentChain : myConcurrentChain) {
                try {
                    myCurrentTerm.addToTerm(courseInConcurrentChain);
                    courseInConcurrentChain.setIsNowUsed(true);
                } catch(Exception courseAlreadyInTerm) {
                    //Do nothing then.
                }
            }
            return myCourse;
        } else {
            myCourse.incrementNextTermIndex();
            
            for(Course courseInConcurrentChain : myConcurrentChain) {
                courseInConcurrentChain.incrementNextTermIndex();
            }
            return myCourse;
        }
    }
    
    public static Course getCurrentCoursePrerequisite(Course myCourse, Term currentTerm,
            int myTermIndex, double myTermLimit, double mySummerTermLimit,
            int myAmountOfTerms, int myYearCount) throws Exception {
        ArrayList<Course> myPrerequisiteProblemCheck = new ArrayList<>();
       
        myPrerequisiteProblemCheck.add(myCourse);
       
        //Implementation of identifying chain suggested by []----[] (Breadth First):
        for(int i = 0; i < myPrerequisiteProblemCheck.size(); i++) {
            Course myCourseInPrerequisiteProblemCheck = myPrerequisiteProblemCheck.get(i);
            
            for(Course myPrerequisiteCourse : myCourseInPrerequisiteProblemCheck.getPreRequisites()) {
                if(!myPrerequisiteProblemCheck.contains(myPrerequisiteCourse)) {
                    myPrerequisiteProblemCheck.add(myPrerequisiteCourse);
                }
            }
        }
        
        boolean markedCourses[] = new boolean[myPrerequisiteProblemCheck.size()];
        boolean coursesOnStack[] = new boolean[myPrerequisiteProblemCheck.size()];
        
        checkForPrerequisiteCircle(myCourse, myPrerequisiteProblemCheck,
                markedCourses, coursesOnStack);
        
        if(!myCourse.getPreRequisites().isEmpty() &&
                !myCourse.getPreRequisites().get(0).getConcurrentCourses().isEmpty()
                && !myCourse.getPreRequisites().get(0).isIsNowUsed()) {
            myCourse.incrementNextTermIndex();
            return concurrentProcessing(myCourse.getPreRequisites().get(0), 
                    currentTerm, myTermLimit, mySummerTermLimit, myTermIndex,
                    myAmountOfTerms, myYearCount);
        }
        
        if(myCourse.getNextTermIndex() == myTermIndex && !myCourse.isIsNowUsed()) {
            if(fitsInThisTerm(myCourse, currentTerm)) {
                if(myCourse.getPreRequisites().isEmpty()) {
                    return myCourse;
                } else if(!myCourse.getPreRequisites().isEmpty() && !hasFullyUsedCourses(myCourse.getPreRequisites())) {
                    myCourse.incrementNextTermIndex();
                    
                    Course myPrerequisiteCourse = myCourse.getPreRequisites().get(0);
                    return getCurrentCoursePrerequisite(myPrerequisiteCourse, 
                            currentTerm, myTermIndex, myTermLimit, mySummerTermLimit,
                            myAmountOfTerms, myYearCount);
                } else if(!myCourse.getPreRequisites().isEmpty() && hasFullyUsedCourses(myCourse.getPreRequisites())) {
                    if(myCourse.getNextTermIndex() == myCourse.getPreRequisites().get(0).getNextTermIndex()) {
                        myCourse.incrementNextTermIndex();
                    } else {
                        return myCourse;
                    }
                }
            } else {
                if(myCourse.getCourseUnits() > myTermLimit && 
                        myCourse.getCourseUnits() > mySummerTermLimit) {
                    throw new Exception(myCourse.getCourseName() + " violates"
                            + " the term limits given.");
                }
                myCourse.incrementNextTermIndex();
                return myCourse;
            }
        }
        return myCourse;
    }
    
    //Used Ivan Voroshilin's Question/Answer for developing a check for cycles
    //in prerequisites (Directed Graph Approach):
    //http://stackoverflow.com/questions/19113189/detecting-cycles-in-a-graph-using-dfs-2-different-approaches-and-whats-the-dif
    public static void checkForPrerequisiteCircle(Course myCourse, 
            ArrayList<Course> myPrerequisiteCollection, boolean markedCourses[],
            boolean coursesOnStack[]) throws Exception {
        int myCourseVisitedLocation = myPrerequisiteCollection.indexOf(myCourse);
        
        if(myCourseVisitedLocation == -1) {
            throw new Exception("Logic error in prerequisite checking. Consult"
                    + " the developer to report this error.");
        }
        
        markedCourses[myCourseVisitedLocation] = true;
        coursesOnStack[myCourseVisitedLocation] = true;
        
        for(Course myPrerequisiteCourse : myCourse.getPreRequisites()) {
            int myPrerequisiteCourseLocation = myPrerequisiteCollection.indexOf(myPrerequisiteCourse);
            
            if(myPrerequisiteCourseLocation == -1) {
                throw new Exception("Logic error in prerequisite checking. Consult"
                        + " the developer to report this error.");
            }
            
            if(!markedCourses[myPrerequisiteCourseLocation]) {
                checkForPrerequisiteCircle(myPrerequisiteCourse, myPrerequisiteCollection,
                        markedCourses, coursesOnStack);
            } else if(coursesOnStack[myPrerequisiteCourseLocation]) {
                throw new Exception(myCourse.getCourseName() + " has an invalid"
                        + " prerequisite pattern.");
            }
        }
        
        coursesOnStack[myCourseVisitedLocation] = false;
    }
    
    public static boolean hasFullyUsedCourses(ArrayList<Course> myCourseList) {
        for(int i = 0; i < myCourseList.size(); i++) {
            Course myCurrentCourse = myCourseList.get(i);
            
            if(!myCurrentCourse.isIsNowUsed()) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean hasSummerCourses(ArrayList<Course> myCourseList) {
        for(int i = 0; i < myCourseList.size(); i++) {
            Course myCurrentCourse = myCourseList.get(i);
            
            if(myCurrentCourse.isSummerCompatible()) {
                return true;
            }
        }
        return false;
    }
    
    public static double getTermLimit() {
        double myTermLimitToSend = 0.0;
        boolean isInErrorState = false;
        Scanner kbd = new Scanner(System.in);
        
        do {
            if(isInErrorState) {
                System.out.print("Invalid Term Units. Try again: ");
            } else {
                System.out.print("Enter Term Limit: ");
            }
            
            try {
                myTermLimitToSend = Double.parseDouble(kbd.nextLine());
                
                if(myTermLimitToSend <= 0) {
                    isInErrorState = true;
                } else {
                    isInErrorState = false;
                }
            } catch(NumberFormatException invalidTermLimit) {
                isInErrorState = true;
            }
        } while(isInErrorState);
        
        return myTermLimitToSend;
    }
    
    public static int askForAmountOfTermsInYear() {
        int myAmountOfTermsInYear = 0;
        boolean isInErrorState = false;
        Scanner kbd = new Scanner(System.in);
        
        do {
            if(isInErrorState) {
                System.out.print("Invalid term count. Try again: ");
            } else {
                System.out.print("Enter amount of terms in year excluding summer: ");
            }
            
            try {
                myAmountOfTermsInYear = Integer.parseInt(kbd.nextLine());
                
                if(myAmountOfTermsInYear <= 0) {
                    isInErrorState = true;
                } else {
                    isInErrorState = false;
                }
            } catch(NumberFormatException invalidTermLimit) {
                isInErrorState = true;
            }
        } while(isInErrorState);
        
        return myAmountOfTermsInYear + 1;
    }
    
    public static boolean fitsInThisTerm(Course myCourse, Term myTerm) {
        if(myCourse.getCourseUnits() + myTerm.getTermCount() <= myTerm.getTermLimit()) {
            return true;
        }
        return false;
    }
}
