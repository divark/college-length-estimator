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
package divarktech.Jesse.Paone;

import divarktech.tyler.schmidt.*;
import static divarktech.tyler.schmidt.CLEImplementation.printTerms;
import static divarktech.tyler.schmidt.CLEImplementation.processTerms;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

//Used the following source to deal with contentText wrapping in many of the
//alerts below:
//http://stackoverflow.com/questions/28937392/javafx-alerts-and-their-size
public class FXMLDocumentController implements Initializable
{
    //Static

    private RawCourseList mainList = new RawCourseList();

    //Added by Tyler:
    @FXML
    private AnchorPane myWindow;

    private Course myCourse = new Course();
    
    //Tyler: Section for Term Exclusive Processing:
    @FXML
    private ComboBox ComboBoxTermExclusive;
    
    @FXML
    private CheckBox checkBoxToggleTermExclusive;
    
    @FXML
    private ListView listViewTermExclusive;
    
    @FXML
    private Button btnAddTermExclusive;
    
    @FXML
    private Button btnRemoveTermExclusive;
    //End Section for Term Exclusive Processing

    //Added by Tyler:
    private String myPastSelection = "";

    //Added by Tyler:
    private String usersInputFileName = "";

    @FXML
    private TableView<Course> mainTable;

    @FXML
    private Button BtnAddNew;

    @FXML
    private Button BtnDel;

    @FXML
    private TextField TermType;

    @FXML
    private TextField UnitLimit;

    //Added by Tyler:
    @FXML
    private TextField txtFldAmountOfTerms;

    //Added by Tyler:
    @FXML
    private TextField txtFldSummerTermLimit;

    @FXML
    private Button BtnProcess;

    @FXML
    private TextField CourseName;

    @FXML
    private TextField Units;

    @FXML
    private CheckBox PrereqCheck;

    @FXML
    private ComboBox Prereqs;

    @FXML
    private ListView<String> prereqList;

    @FXML
    private Button AddPrereq;

    @FXML
    private Button RemovePrereq;

    @FXML
    private CheckBox ConcurCheck;

    @FXML
    private ComboBox Concurs;

    @FXML
    private ListView<String> concurList;

    @FXML
    private Button AddConcur;

    @FXML
    private Button RemoveConcur;

    @FXML
    private CheckBox Summer;

    private boolean error = false;
    //Added by Tyler:
    private boolean hasUnsavedData = false;

    private boolean isInErrorState = false;

    //String curEdit = "";

    //Non-Static
    @FXML
    final ObservableList<String> CurrentPrereqs = FXCollections.observableArrayList();
    @FXML
    final ObservableList<String> CurrentConcurs = FXCollections.observableArrayList();
    @FXML
    final ObservableList<String> AvailableCourses = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Course, String> courseNameCol;
    @FXML
    private TableColumn<Course, Double> unitsCol;
    @FXML
    private TableColumn<Course, String> prerequsiteCol;
    @FXML
    private TableColumn<Course, String> concurrentCol;
    @FXML
    private TableColumn<Course, String> summerCol;
    //Added by Tyler:
    @FXML
    private TableColumn<Course, String> tblColTermExclusive;

    //Functions Start
    //Started by Jesse, modified by Tyler:
    @FXML
    private void BtnProcessClick(ActionEvent event)
    {
        if (TermType.getText().equals(""))
        {
            TermType.requestFocus();
        }
        else if (TermType.getText().length() > 25)
        {
            Alert myWarningPrompt = new Alert(AlertType.ERROR);
            myWarningPrompt.setTitle("Term Type Warning");
            myWarningPrompt.setHeaderText("Term Type Length Warning");
            myWarningPrompt.setContentText("Large Term Type detected. Please revise.");
            myWarningPrompt.showAndWait();

            TermType.requestFocus();
        }
        else if (UnitLimit.getText().equals(".") || UnitLimit.getText().equals("") || Double.parseDouble(UnitLimit.getText()) == 0)
        {
            UnitLimit.requestFocus();
        }
        else if (!txtFldAmountOfTerms.isDisabled() && (txtFldAmountOfTerms.getText().equals("") || Integer.parseInt(txtFldAmountOfTerms.getText()) == 0))
        {
            txtFldAmountOfTerms.requestFocus();
        }
        else if (!txtFldSummerTermLimit.isDisabled() && (txtFldSummerTermLimit.getText().equals(".") || txtFldSummerTermLimit.getText().equals("") || Double.parseDouble(UnitLimit.getText()) == 0))
        {
            txtFldSummerTermLimit.requestFocus();
        }
        else
        {
            for (Course courseInMainList : mainList.getMyRawCourseList())
            {
                if (!courseInMainList.equals(myCourse))
                {
                    if (courseInMainList.getCourseName().toLowerCase().equals(myCourse.getCourseName().toLowerCase()))
                    {
                        Alert myErrorPrompt = new Alert(AlertType.ERROR);
                        myErrorPrompt.getDialogPane().getChildren().stream()
                            .filter(node -> node instanceof Label).forEach(node -> 
                                    ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                        myErrorPrompt.setTitle("Course Conflict");
                        myErrorPrompt.setHeaderText("Course Name Conflict");
                        myErrorPrompt.setContentText(String.format("There already "
                                + "exists a version of %s in the main list", myCourse.getCourseName()));
                        myErrorPrompt.showAndWait();

                        CourseName.requestFocus();
                        mainTable.getSelectionModel().clearAndSelect(mainList.getMyRawCourseList().indexOf(myCourse));
                        isInErrorState = true;
                        return;
                    }
                }
            }
            isInErrorState = false;

            ArrayList<Term> myTermList = null;
            if (txtFldSummerTermLimit.isDisabled())
            {
                try
                {
                    myTermList = processTerms(Double.parseDouble(UnitLimit.getText()),
                            0, Integer.parseInt(txtFldAmountOfTerms.getText()), mainList);
                }
                catch (Exception interpretiveError)
                {
                    Alert myErrorPrompt = new Alert(AlertType.ERROR);
                    myErrorPrompt.getDialogPane().getChildren().stream()
                        .filter(node -> node instanceof Label).forEach(node -> 
                                ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    myErrorPrompt.setTitle("Processing Error");
                    myErrorPrompt.setHeaderText("Processing Error in Given Data");
                    myErrorPrompt.setContentText(interpretiveError.getMessage());
                    myErrorPrompt.showAndWait();
                    return;
                }
            }
            else
            {
                try
                {
                    myTermList = processTerms(Double.parseDouble(UnitLimit.getText()), Double.parseDouble(txtFldSummerTermLimit.getText()), Integer.parseInt(txtFldAmountOfTerms.getText()), mainList);
                }
                catch (Exception interpretiveError)
                {
                    Alert myErrorPrompt = new Alert(AlertType.ERROR);
                    myErrorPrompt.getDialogPane().getChildren().stream()
                        .filter(node -> node instanceof Label).forEach(node -> 
                                ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    myErrorPrompt.setTitle("Processing Error");
                    myErrorPrompt.setHeaderText("Processing Error in Given Data");
                    myErrorPrompt.setContentText(interpretiveError.getMessage());
                    myErrorPrompt.showAndWait();
                    return;
                }
            }

            for (Course courseInRawCourseList : mainList.getMyRawCourseList())
            {
                courseInRawCourseList.usageReset();
            }

            Alert myResults = new Alert(AlertType.INFORMATION);
            myResults.setTitle("Results");
            myResults.setHeaderText("Here's your estimate:");
            String processedTerms = "";

            if (txtFldSummerTermLimit.isDisabled())
            {
                processedTerms = printTerms(myTermList, TermType.getText(),
                        0, Double.parseDouble(UnitLimit.getText()), 0);
                myResults.setContentText(processedTerms);
            }
            else
            {
                processedTerms = printTerms(myTermList, TermType.getText(),
                        Integer.parseInt(txtFldAmountOfTerms.getText()),
                        Double.parseDouble(UnitLimit.getText()),
                        Double.parseDouble(txtFldSummerTermLimit.getText()));
                myResults.setContentText(processedTerms);
            }

            ButtonType clickSaveToContinue = new ButtonType("Save");
            ButtonType clickResetToGenerateNewResults = new ButtonType("Retry");
            ButtonType clickCancelToStop = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

            myResults.getButtonTypes().setAll(clickSaveToContinue, clickResetToGenerateNewResults, clickCancelToStop);

            Optional<ButtonType> buttonClicked = myResults.showAndWait();

            if (buttonClicked.get() == clickSaveToContinue)
            {
                for (Course courseInMyRawCourseList : mainList.getMyRawCourseList())
                {
                    courseInMyRawCourseList.usageReset();
                }

                FileChooser fileSeeker = new FileChooser();
                fileSeeker.setTitle("Enter file name");
                fileSeeker.getExtensionFilters().addAll(
                        new ExtensionFilter("Text Files", "*.txt", "*.text", "*.rtf"),
                        new ExtensionFilter("All Files", "*.*"));
                fileSeeker.setInitialFileName("myCollegePlanResults.txt");
                File usersFile = fileSeeker.showSaveDialog(null);

                if (usersFile == null)
                {
                    return;
                }
                String usersResultFileName = usersFile.getAbsolutePath();

                try
                {
                    FileWriter fileCreator = new FileWriter(new File(usersResultFileName));

                    fileCreator.write(processedTerms);
                    fileCreator.close();
                    return;
                }
                catch (IOException fileAccessDenied)
                {
                    Alert myErrorAlert = new Alert(Alert.AlertType.ERROR);
                    myErrorAlert.setTitle("File Reading Error");
                    myErrorAlert.setContentText("File Reading Error: " + fileAccessDenied.getMessage());
                    myErrorAlert.showAndWait();
                    return;
                }
            }

            while (buttonClicked.get() == clickResetToGenerateNewResults)
            {
                for (Course courseInMyRawCourseList : mainList.getMyRawCourseList())
                {
                    courseInMyRawCourseList.usageReset();
                }

                if (txtFldSummerTermLimit.isDisabled())
                {
                    try
                    {
                        myTermList = processTerms(Double.parseDouble(UnitLimit.getText()),
                            0, Integer.parseInt(txtFldAmountOfTerms.getText()), mainList);
                    }
                    catch (Exception interpretiveError)
                    {
                        Alert myErrorPrompt = new Alert(AlertType.ERROR);
                        myErrorPrompt.getDialogPane().getChildren().stream()
                            .filter(node -> node instanceof Label).forEach(node -> 
                                    ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                        myErrorPrompt.setTitle("Processing Error");
                        myErrorPrompt.setHeaderText("Processing Error in Given Data");
                        myErrorPrompt.setContentText(interpretiveError.getMessage());
                        myErrorPrompt.showAndWait();
                        return;
                    }
                }
                else
                {
                    try
                    {
                        myTermList = processTerms(Double.parseDouble(UnitLimit.getText()),
                                Double.parseDouble(txtFldSummerTermLimit.getText()),
                                Integer.parseInt(txtFldAmountOfTerms.getText()), mainList);
                    }
                    catch (Exception interpretiveError)
                    {
                        Alert myErrorPrompt = new Alert(AlertType.ERROR);
                        myErrorPrompt.getDialogPane().getChildren().stream()
                            .filter(node -> node instanceof Label).forEach(node -> 
                                    ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                        myErrorPrompt.setTitle("Processing Error");
                        myErrorPrompt.setHeaderText("Processing Error in Given Data");
                        myErrorPrompt.setContentText(interpretiveError.getMessage());
                        myErrorPrompt.showAndWait();
                        return;
                    }
                }

                myResults = new Alert(AlertType.CONFIRMATION);
                myResults.setTitle("Results");
                myResults.setHeaderText("Here's your estimate:");
                if (txtFldSummerTermLimit.isDisabled())
                {
                    processedTerms = printTerms(myTermList, TermType.getText(),
                            0, Double.parseDouble(UnitLimit.getText()), 0);
                    myResults.setContentText(processedTerms);
                }
                else
                {
                    processedTerms = printTerms(myTermList, TermType.getText(),
                            Integer.parseInt(txtFldAmountOfTerms.getText()),
                            Double.parseDouble(UnitLimit.getText()),
                            Double.parseDouble(txtFldSummerTermLimit.getText()));
                    myResults.setContentText(processedTerms);
                }

                clickSaveToContinue = new ButtonType("Save");
                clickResetToGenerateNewResults = new ButtonType("Retry");
                clickCancelToStop = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

                myResults.getButtonTypes().setAll(clickSaveToContinue, 
                        clickResetToGenerateNewResults, clickCancelToStop);

                buttonClicked = myResults.showAndWait();

                for (Course myCourseInRawCourseList : mainList.getMyRawCourseList())
                {
                    myCourseInRawCourseList.usageReset();
                }

                if (buttonClicked.get() == clickSaveToContinue)
                {
                    FileChooser fileSeeker = new FileChooser();
                    fileSeeker.setTitle("Enter file name");
                    fileSeeker.getExtensionFilters().addAll(
                            new ExtensionFilter("Text Files", "*.txt", "*.text", "*.rtf"),
                            new ExtensionFilter("All Files", "*.*"));
                    fileSeeker.setInitialFileName("myCollegePlanResults.txt");
                    File usersFile = fileSeeker.showSaveDialog(null);

                    if (usersFile == null)
                    {
                        return;
                    }
                    String usersResultFileName = usersFile.getAbsolutePath();

                    try
                    {
                        FileWriter fileCreator = new FileWriter(new File(usersResultFileName));

                        fileCreator.write(processedTerms);
                        fileCreator.close();
                        return;
                    }
                    catch (IOException fileAccessDenied)
                    {
                        Alert myErrorAlert = new Alert(Alert.AlertType.ERROR);
                        myErrorAlert.setTitle("File Reading Error");
                        myErrorAlert.setContentText("File Reading Error: " + 
                                fileAccessDenied.getMessage());
                        myErrorAlert.showAndWait();
                        return;
                    }
                }
            }
        }
    }

    //Tyler: Added summer checking to determine if text fields are to be set
    // to be editable.
    @FXML
    private void BtnAddNewClick(ActionEvent event)
    {
        if (CourseName.getText().equals(""))
        {
            CourseName.requestFocus();
        }
        else
        {
            try
            {
                myCourse.setCourseName(CourseName.getText().trim());
            }
            
            catch (Exception ex)
            {
                CourseName.requestFocus();
            }

            if (Units.getText().equals(".") || Units.getText().equals("") || 
                    Double.parseDouble(Units.getText()) == 0)
            {
                Units.requestFocus();
            }
            else
            {
                try
                {
                    myCourse.setCourseUnits(Double.parseDouble(Units.getText()));
                }
                catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                }

                if (PrereqCheck.isSelected() && !CurrentPrereqs.isEmpty())
                {
                    for(String myCoursePrereqName : CurrentPrereqs)
                    {
                        try
                        {
                            Course myCoursePrereq = mainList.getCourseByName(myCoursePrereqName);
                            myCourse.addPrerequisite(myCoursePrereq);
                        }
                        catch(Exception invalidFetchOrAdd)
                        {
                            Alert myErrorMessage = new Alert(AlertType.ERROR);
                            myErrorMessage.setTitle("Course Add Error");
                            myErrorMessage.setHeaderText("Course Add Invalid");
                            myErrorMessage.setContentText("Course Add Error: " + 
                                    invalidFetchOrAdd.getMessage());
                            myErrorMessage.showAndWait();
                            return;
                        }
                    }
                    //SetPrereqs();
                    PrintPrereqs();
                }

                if (ConcurCheck.isSelected() && !CurrentConcurs.isEmpty())
                {
                    for(String myCourseConcurName : CurrentConcurs)
                    {
                        try
                        {
                            Course myCourseConcur = mainList.getCourseByName(myCourseConcurName);
                            myCourse.addConcurrent(myCourseConcur);
                            
                            updateConcurrentsInCourse(myCourseConcur);
                        }
                        catch(Exception invalidFetchOrAdd)
                        {
                            Alert myErrorMessage = new Alert(AlertType.ERROR);
                            myErrorMessage.setTitle("Course Add Error");
                            myErrorMessage.setHeaderText("Course Add Invalid");
                            myErrorMessage.setContentText("Course Add Error: " + 
                                    invalidFetchOrAdd.getMessage());
                            myErrorMessage.showAndWait();
                            return;
                        }
                    }
                    //SetConcurs();
                    PrintConcurs();
                }

                SummerSet();
                updateComboBoxTermExclusive();

                try
                {
                    mainList.addToList(myCourse);
                    mainTable.getItems().add(myCourse);
                    myPastSelection = "";
                }
                catch (Exception CannotAddToMains)
                {
                    Alert myErrorThatOccured = new Alert(AlertType.ERROR);
                    myErrorThatOccured.setTitle("Course Processing Error");
                    myErrorThatOccured.setHeaderText("Course Processing Error");
                    myErrorThatOccured.setContentText(CannotAddToMains.getMessage());

                    myErrorThatOccured.showAndWait();
                    return;
                }
                
                myCourse = new Course();
                setNewAvailable();
                CourseName.clear();
                Units.clear();
                CurrentPrereqs.clear();
                CurrentConcurs.clear();
                PrereqCheck.setSelected(false);
                PrereqCheckClick(event);
                ConcurCheck.setSelected(false);
                ConcurCheckClick(event);
                Summer.setSelected(false);
                BtnProcess.setDisable(false);
                hasUnsavedData = true;
            }
        }
        makeSummerFieldsEditableIfSummerCoursesExist();
        updateComboBoxTermExclusive();
    }

    @FXML
    private void ifDeleteKeyPressed(KeyEvent userInput)
    {
        if (userInput.getCode().equals(KeyCode.DELETE) || 
                userInput.getCode().equals(KeyCode.BACK_SPACE))
        {
            ActionEvent event = new ActionEvent();
            BtnDelClick(event);
        }
    }

    //Tyler: Modified to delete what is selected on the table.
    @FXML
    private void BtnDelClick(ActionEvent event)
    {
        if (mainTable.getSelectionModel().getSelectedItem() == null)
        {
            mainTable.getSelectionModel().clearSelection();
            mainTable.requestFocus();
        }
        else
        {
            Alert deleteConfirmationWindow = new Alert(AlertType.CONFIRMATION);
            deleteConfirmationWindow.getDialogPane().getChildren().stream()
                    .filter(node -> node instanceof Label).forEach(node -> 
                            ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            deleteConfirmationWindow.setTitle("Course Removal");
            deleteConfirmationWindow.setHeaderText("Course Removal Confirmation");
            deleteConfirmationWindow.setContentText(String.format("Are you "
                    + "sure you want to remove %s?",
                    mainTable.getSelectionModel().getSelectedItem().getCourseName()));

            Optional<ButtonType> userRequest = deleteConfirmationWindow.showAndWait();
            if (userRequest.get() == ButtonType.OK)
            {
                try
                {
                    AvailableCourses.remove(mainTable.getSelectionModel().getSelectedItem().getCourseName());
                    mainList.removeCourseByName(mainTable.getSelectionModel().getSelectedItem().getCourseName());

                    for (Course myCurrentCourse : mainList.getMyRawCourseList())
                    {
                        myCourse = myCurrentCourse;
                        myCourse.setPrerequisitesGUIWorkAround("");
                        myCourse.setConcurrentGUIWorkAround("");
                        PrintPrereqs();
                        PrintConcurs();
                    }

                    CourseName.clear();
                    Units.clear();
                    CurrentPrereqs.clear();
                    CurrentConcurs.clear();
                    PrereqCheck.setSelected(false);
                    Prereqs.setDisable(true);
                    AddPrereq.setDisable(true);
                    RemovePrereq.setDisable(true);
                    prereqList.setDisable(true);
                    ConcurCheck.setSelected(false);
                    Concurs.setDisable(true);
                    AddConcur.setDisable(true);
                    RemoveConcur.setDisable(true);
                    concurList.setDisable(true);
                    Summer.setSelected(false);

                    //Workaround used from Daniel De Leon's answer:
                    //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
                    RefreshTable();
                    myCourse = new Course();
                    myPastSelection = "";

                    if (mainList.getMyRawCourseList().isEmpty())
                    {
                        BtnProcess.setDisable(true);
                    }
                }
                catch (Exception courseNotFound)
                {
                    System.out.println(courseNotFound.getMessage());
                }
                hasUnsavedData = true;
            }
            else
            {
                //Do nothing then.
            }
        }
    }

    //Tyler: Modified to update course units in real time.
    @FXML
    private void setCourseUnitsOnKeyPressed(KeyEvent event)
    {    
        try
        {
            String myUserInput = Units.getText();
            
            Double myNewCourseUnits = Double.parseDouble(myUserInput);
            myCourse.setCourseUnits(myNewCourseUnits);
            hasUnsavedData = true;
        }
        catch(Exception invalidNumber)
        {
            event.consume();
            return;
        }
        //Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
    }

    //Tyler: Modified so when the user deselects the course having prerequisites,
    // it clears the prereqlist for the course and removes references from
    // every course with a prerequisite in mainlist.
    @FXML
    private void PrereqCheckClick(ActionEvent event)
    {
        if (PrereqCheck.isSelected())
        {
            Prereqs.setDisable(false);
            AddPrereq.setDisable(false);
            RemovePrereq.setDisable(false);
            prereqList.setDisable(false);
        }
        else if (!prereqList.getItems().isEmpty())
        {
            Alert prerequisiteRemovalAlert = new Alert(AlertType.CONFIRMATION);
            prerequisiteRemovalAlert.getDialogPane().getChildren().stream()
                    .filter(node -> node instanceof Label).forEach(node -> 
                            ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            prerequisiteRemovalAlert.setTitle("Prerequisite Removal Confirmation");
            prerequisiteRemovalAlert.setHeaderText("Prerequisite Removal Confirmation");
            prerequisiteRemovalAlert.setContentText("Unchecking this will remove "
                    + "all prerequisites from this course. Are you sure you "
                    + "want to do this?");
            Optional<ButtonType> myUserInput = prerequisiteRemovalAlert.showAndWait();
            if (myUserInput.get() == ButtonType.OK)
            {
                Prereqs.setDisable(true);
                AddPrereq.setDisable(true);
                RemovePrereq.setDisable(true);
                prereqList.setDisable(true);

                for (Course myCourseInMainList : mainList.getMyRawCourseList())
                {
                    if (myCourseInMainList.getPreRequisites().contains(myCourse))
                    {
                        try
                        {
                            myCourseInMainList.removePrerequisiteByName(myCourse.getCourseName());
                            updatePrerequisitesInCourse(myCourseInMainList);
                        }
                        catch (Exception cannotFindCourse)
                        {
                            System.out.println(myCourseInMainList.getCourseName() + " lied!");
                        }
                    }
                }
                myCourse.getPreRequisites().clear();
                PrintPrereqs();
                prereqList.getItems().clear();
            }
            else
            {
                PrereqCheck.setSelected(true);
                return;
            }
        }
        else
        {
            Prereqs.setDisable(true);
            AddPrereq.setDisable(true);
            RemovePrereq.setDisable(true);
            prereqList.setDisable(true);
        }
        RefreshTable();
        hasUnsavedData = true;
    }

    //Tyler: Modified to behave better if a prerequisite is added to a non-existant
    //course.
    @FXML
    private void AddPrereqClick(ActionEvent event)
    {
        if (Prereqs.getSelectionModel().isEmpty())
        {
            return;
        }
        
        if (Prereqs.getSelectionModel().getSelectedItem().toString().equals(myCourse.getCourseName()))
        {
            Alert myErrorThatOccured = new Alert(AlertType.ERROR);
            myErrorThatOccured.setTitle("Course Processing Error");
            myErrorThatOccured.setHeaderText("Course Processing Error");
            myErrorThatOccured.setContentText(myCourse.getCourseName()
                    + " cannot be its own prerequisite.");

            myErrorThatOccured.showAndWait();
            return;
        }
        //Redundent Prerequisite Add in Concurrent Chain Check:
        else if(!myCourse.getConcurrentCourses().isEmpty())
        {
            for(Course concurrentCourseInMyCourse: myCourse.getConcurrentCourses()) 
            {
                if(!concurrentCourseInMyCourse.getPreRequisites().isEmpty()) 
                {
                    for(Course myPrerequisiteCourseCheck : concurrentCourseInMyCourse.getPreRequisites())
                    {
                        if(Prereqs.getSelectionModel().getSelectedItem().toString().equals(myPrerequisiteCourseCheck.getCourseName()))
                        {
                            Alert myRedundentWarning = new Alert(AlertType.WARNING);
                            myRedundentWarning.getDialogPane().getChildren().stream()
                                .filter(node -> node instanceof Label).forEach(node -> 
                                        ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                            myRedundentWarning.setTitle("Course Processing Warning");
                            myRedundentWarning.setHeaderText("Redundent Prerequisite Add");
                            myRedundentWarning.setContentText(String.format("%s's "
                                    + "concurrent chain already has %s as a "
                                    + "prerequisite. Omitting.", 
                                    myCourse.getCourseName(), 
                                    Prereqs.getSelectionModel().getSelectedItem().toString()));
                            myRedundentWarning.showAndWait();
                            return;
                        }
                    }
                }
            }
        }

        boolean add = true;

        for (int i = 0; i < CurrentPrereqs.size(); i++)
        {
            if (CurrentPrereqs.get(i).equals(Prereqs.getValue().toString()))
            {
                add = false;
            }
        }

        if (add)
        {
            try
            {
                try
                {
                    mainList.getCourseByName(myCourse.getCourseName());
                }
                catch(Exception courseDoesntExistYet)
                {
                    if(!CurrentConcurs.contains(Prereqs.getValue().toString()))
                    {
                        CurrentPrereqs.add(Prereqs.getValue().toString());
                        prereqList.setItems(CurrentPrereqs);
                        return;
                    }
                    else if(CurrentPrereqs.contains(Prereqs.getValue().toString()))
                    {
                        return;
                    }
                    else
                    {
                        Alert myErrorThatOccured = new Alert(AlertType.ERROR);
                        myErrorThatOccured.setTitle("Course Processing Error");
                        myErrorThatOccured.setHeaderText("Course Processing Error");
                        myErrorThatOccured.setContentText(Prereqs.getValue().toString()
                        + " is already in concurrents");

                        myErrorThatOccured.showAndWait();
                        return;
                    }
                }
                CurrentPrereqs.add(Prereqs.getValue().toString());
                myCourse.addPrerequisite(mainList.getCourseByName(Prereqs.getValue().toString()));
                PrintPrereqs();
                hasUnsavedData = true;
            }
            catch (Exception interpretiveError)
            {
                Alert myErrorThatOccured = new Alert(AlertType.ERROR);
                myErrorThatOccured.setTitle("Course Processing Error");
                myErrorThatOccured.setHeaderText("Course Processing Error");
                myErrorThatOccured.setContentText(interpretiveError.getMessage());

                myErrorThatOccured.showAndWait();
                CurrentPrereqs.remove(Prereqs.getValue().toString());
                return;
            }

            prereqList.setItems(CurrentPrereqs);
            //Workaround used from Daniel De Leon's answer:
            //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
            mainTable.getColumns().get(0).setVisible(false);
            mainTable.getColumns().get(0).setVisible(true);
        }
    }

    //Tyler: Modified so that it removes prerequisite course on selection in prereqList
    @FXML
    private void RemovePrereqClick(ActionEvent event)
    {
        if(prereqList.getSelectionModel().getSelectedItem() == null)
        {
            return;
        }
        
        try
        {
            String myPrerequisiteToRemove = prereqList.getSelectionModel().getSelectedItem();
            
            try
            {
                mainList.getCourseByName(myCourse.getCourseName());
            }
            catch(Exception courseDoesntExistYet)
            {
                CurrentPrereqs.remove(myPrerequisiteToRemove);
                concurList.setItems(CurrentPrereqs);
                return;
            }
            
            myCourse.removePrerequisiteByName(myPrerequisiteToRemove);
            CurrentPrereqs.remove(myPrerequisiteToRemove);
            PrintPrereqs();
            prereqList.setItems(CurrentPrereqs);
            hasUnsavedData = true;
        }
        catch(Exception courseNotFoundError)
        {
            Alert myErrorAlert = new Alert(AlertType.ERROR);
            myErrorAlert.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label).forEach(node -> 
                        ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            myErrorAlert.setTitle("Prerequisite Removal Error");
            myErrorAlert.setHeaderText("Prerequisite not found");
            myErrorAlert.setContentText(courseNotFoundError.getMessage());
            myErrorAlert.showAndWait();
            return;
        }
        
        //Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
    }

    //Tyler: Modified so when the user deselects the course having concurrents,
    // it clears the concurlist for the course and removes references from
    // every course with a concurrent in mainlist.
    @FXML
    private void ConcurCheckClick(ActionEvent event)
    {
        if (ConcurCheck.isSelected())
        {
            Concurs.setDisable(false);
            AddConcur.setDisable(false);
            RemoveConcur.setDisable(false);
            concurList.setDisable(false);
        }
        else if (!concurList.getItems().isEmpty())
        {
            Alert concurrentRemovalAlert = new Alert(AlertType.CONFIRMATION);
            concurrentRemovalAlert.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label).forEach(node -> 
                        ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            concurrentRemovalAlert.setTitle("Concurrent Removal Confirmation");
            concurrentRemovalAlert.setHeaderText("Concurrent Removal Confirmation");
            concurrentRemovalAlert.setContentText("Unchecking this will remove "
                    + "all concurrents from this course. Are you sure you "
                    + "want to do this?");
            Optional<ButtonType> myUserInput = concurrentRemovalAlert.showAndWait();
            if (myUserInput.get() == ButtonType.OK)
            {
                Concurs.setDisable(true);
                AddConcur.setDisable(true);
                RemoveConcur.setDisable(true);
                concurList.setDisable(true);
                for (Course myCourseInMainList : mainList.getMyRawCourseList())
                {
                    if (myCourseInMainList.getConcurrentCourses().contains(myCourse))
                    {
                        try
                        {
                            myCourseInMainList.removeConcurrentByName(myCourse.getCourseName());
                            updateConcurrentsInCourse(myCourseInMainList);
                        }
                        catch (Exception cannotFindCourse)
                        {
                            System.out.println(myCourseInMainList.getCourseName() + " lied!");
                        }
                    }
                }
                myCourse.getConcurrentCourses().clear();
                PrintConcurs();
                concurList.getItems().clear();
            }
            else
            {
                ConcurCheck.setSelected(true);
                return;
            }
        }
        else
        {
            Concurs.setDisable(true);
            AddConcur.setDisable(true);
            RemoveConcur.setDisable(true);
            concurList.setDisable(true);
        }
        RefreshTable();
        hasUnsavedData = true;
    }

    //Tyler: Modified to behave better when adding concurrents in a course that
    //does not exist yet.
    @FXML
    private void AddConcurClick(ActionEvent event)
    {

        if (Concurs.getSelectionModel().isEmpty())
        {
            return;
        }

        if (Concurs.getSelectionModel().getSelectedItem().toString().equals(myCourse.getCourseName()))
        {
            Alert myErrorThatOccured = new Alert(AlertType.ERROR);
            myErrorThatOccured.setTitle("Course Processing Error");
            myErrorThatOccured.setHeaderText("Course Processing Error");
            myErrorThatOccured.setContentText(myCourse.getCourseName() + " "
                    + "cannot be its own concurrent.");

            myErrorThatOccured.showAndWait();
            return;
        }
        
        try
        {
            try
            {
                mainList.getCourseByName(myCourse.getCourseName());
            }
            catch(Exception courseDoesntExistYet)
            {
                if(!CurrentPrereqs.contains(Concurs.getValue().toString())) {
                    CurrentConcurs.add(Concurs.getValue().toString());
                    concurList.setItems(CurrentConcurs);
                    return;
                }
                else if(CurrentConcurs.contains(Concurs.getValue().toString()))
                {
                    return;
                }
                else
                {
                    Alert myErrorThatOccured = new Alert(AlertType.ERROR);
                    myErrorThatOccured.setTitle("Course Processing Error");
                    myErrorThatOccured.setHeaderText("Course Processing Error");
                    myErrorThatOccured.setContentText(Concurs.getValue().toString()
                    + " is already in prerequisites.");

                    myErrorThatOccured.showAndWait();
                    return;
                }
            }
            myCourse.addConcurrent(mainList.getCourseByName(Concurs.getValue().toString()));
            CurrentConcurs.add(Concurs.getValue().toString());
            PrintConcurs();
            myCourse = mainList.getCourseByName(Concurs.getValue().toString());
            PrintConcurs();
            concurList.setItems(CurrentConcurs);
            myCourse = mainList.getCourseByName(myPastSelection);
            concurList.setItems(CurrentConcurs);
            hasUnsavedData = true;
        }
        catch (Exception interpretiveError)
        {
            Alert myErrorThatOccured = new Alert(AlertType.ERROR);
            myErrorThatOccured.setTitle("Course Processing Error");
            myErrorThatOccured.setHeaderText("Course Processing Error");
            myErrorThatOccured.setContentText(interpretiveError.getMessage());

            myErrorThatOccured.showAndWait();
            return;
        }

        //Tyler: Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
    }

    //Tyler: Modified so that it removes concurrent course on selection in concurList
    @FXML
    private void RemoveConcurClick(ActionEvent event)
    {
        if(concurList.getSelectionModel().getSelectedItem() == null)
        {
            return;
        }
        
        try 
        {
            String myConcurrentToRemove = concurList.getSelectionModel().getSelectedItem();
            
            try
            {
                mainList.getCourseByName(myCourse.getCourseName());
            }
            catch(Exception courseDoesntExistYet)
            {
                CurrentConcurs.remove(myConcurrentToRemove);
                concurList.setItems(CurrentConcurs);
                return;
            }
            
            myCourse.removeConcurrentByName(concurList.getSelectionModel().getSelectedItem());
            CurrentConcurs.remove(myConcurrentToRemove);
            PrintConcurs();
            myCourse = mainList.getCourseByName(myConcurrentToRemove);
            PrintConcurs();
            concurList.setItems(CurrentConcurs);
            myCourse = mainList.getCourseByName(myPastSelection);
            concurList.setItems(CurrentConcurs);
            hasUnsavedData = true;
        }
        catch(Exception courseNotFoundError)
        {
            Alert myErrorAlert = new Alert(AlertType.ERROR);
            myErrorAlert.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label).forEach(node -> 
                        ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            myErrorAlert.setTitle("Concurrent Removal Error");
            myErrorAlert.setHeaderText("Concurrent not found");
            myErrorAlert.setContentText(courseNotFoundError.getMessage());
            myErrorAlert.showAndWait();
            return;
        }
        
            //Tyler: Workaround used from Daniel De Leon's answer:
            //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
            mainTable.getColumns().get(0).setVisible(false);
            mainTable.getColumns().get(0).setVisible(true);
    }
    
    @FXML
    private void BtnAddTermExclusiveClick(ActionEvent userHasClicked)
    {
        if(ComboBoxTermExclusive.getSelectionModel().isEmpty())
        {
            return;
        }
        
        if(!myCourse.getTermExclusiveIdentifiers().contains(
                Integer.parseInt(ComboBoxTermExclusive.getSelectionModel()
                        .getSelectedItem().toString()))) {
            myCourse.getTermExclusiveIdentifiers().add(Integer.parseInt(
                    ComboBoxTermExclusive.getSelectionModel().getSelectedItem()
                            .toString()));
            listViewTermExclusive.getItems().add(ComboBoxTermExclusive
                    .getSelectionModel().getSelectedItem().toString());
            
            myCourse.setTermExclusiveGUIWorkAround("Yes");
        }
        //Tyler: Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
        hasUnsavedData = true;
    }
    
    @FXML
    private void BtnRemoveTermExclusiveClick(ActionEvent userHasClicked)
    {
        if(listViewTermExclusive.getSelectionModel().getSelectedItem() == null)
        {
            return;
        }

        if(myCourse.getTermExclusiveIdentifiers().contains(
                Integer.parseInt(listViewTermExclusive.getSelectionModel()
                        .getSelectedItem().toString()))) {
            Integer myTermExclusive = Integer.parseInt(
                    listViewTermExclusive.getSelectionModel().getSelectedItem().toString());
            
            myCourse.getTermExclusiveIdentifiers().remove(myTermExclusive);
            listViewTermExclusive.getItems().remove(ComboBoxTermExclusive
                    .getSelectionModel().getSelectedItem().toString());
            
            if(myCourse.getTermExclusiveIdentifiers().isEmpty()) 
            {
                myCourse.setTermExclusiveGUIWorkAround("No");
            }
        }
        //Tyler: Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
        hasUnsavedData = true;
    }

    private void setNewAvailable()
    {
        AvailableCourses.clear();

        for (int i = 0; i < mainList.getMyRawCourseList().size(); i++)
        {
            AvailableCourses.add(mainList.getMyRawCourseList().get(i).getCourseName());
        }
    }

    @FXML
    private void SummerSet()
    {
        if (Summer.isSelected())
        {
            myCourse.setSummerCompatible(true);
            myCourse.setSummerCompatibleGUIWorkAround("Yes");
        }
        else
        {
            myCourse.setSummerCompatible(false);
            myCourse.setSummerCompatibleGUIWorkAround("No");
        }

        makeSummerFieldsEditableIfSummerCoursesExist();
        updateComboBoxTermExclusive();
        //Tyler: Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
        hasUnsavedData = true;
    }

    //Tyler: Added to make txtFlds editable relevant to summer processing
    private void makeSummerFieldsEditableIfSummerCoursesExist()
    {
        for (Course myCurrentCourse : mainList.getMyRawCourseList())
        {
            if (myCurrentCourse.isSummerCompatible())
            {
                txtFldAmountOfTerms.setDisable(false);
                txtFldSummerTermLimit.setDisable(false);
                return;
            }
        }
        //txtFldAmountOfTerms.setDisable(true);
        txtFldSummerTermLimit.setDisable(true);
    }

    private void RefreshTable()
    {
        mainTable.getItems().clear();

        for (int i = 0; i < mainList.getMyRawCourseList().size(); i++)
        {
            String toAddName = mainList.getMyRawCourseList().get(i).getCourseName();

            try
            {
                mainTable.getItems().add(mainList.getCourseByName(toAddName));
            }
            catch (Exception NoRefresh)
            {
                System.out.println(NoRefresh.getMessage());
            }
        }
        //Tyler: Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
    }

    //Set Prereqs & Concurrents
    private void SetPrereqs()
    {
        for (int i = 0; i < CurrentPrereqs.size(); i++)
        {
            String name = CurrentPrereqs.get(i);

            try
            {
                myCourse.addPrerequisite(mainList.getCourseByName(name));
            }
            catch (Exception CannotSetPrereq)
            {
                System.out.println(CannotSetPrereq.getMessage());
            }
        }
    }

    private void SetConcurs()
    {
        for (int i = 0; i < CurrentConcurs.size(); i++)
        {
            String name = CurrentConcurs.get(i);

            try
            {
                myCourse.addConcurrent(mainList.getCourseByName(name));
            }
            catch (Exception CannotSetConcurs)
            {
                System.out.println(CannotSetConcurs.getMessage());
                error = true;
            }
        }
    }

    //Clear Prereqs & Concurrents
    private void ClearPrereqs()
    {
        int prNum = myCourse.getPreRequisites().size();

        for (int i = 0; i < prNum; i++)
        {
            String name = myCourse.getPreRequisites().get(0).getCourseName();

            try
            {
                myCourse.removePrerequisiteByName(name);
            }
            catch (Exception CannotClearPrereq)
            {
                System.out.println(CannotClearPrereq.getMessage());
            }
        }

        myCourse.setPrerequisitesGUIWorkAround("");
    }

    private void ClearConcurs()
    {
        int ccNum = myCourse.getConcurrentCourses().size();
        for (int i = 0; i < ccNum; i++)
        {
            String name = myCourse.getConcurrentCourses().get(0).getCourseName();

            try
            {
                myCourse.removeConcurrentByName(name);
            }
            catch (Exception CannotClearConcurs)
            {
                System.out.println(CannotClearConcurs.getMessage());
            }
        }

        myCourse.setConcurrentGUIWorkAround("");
    }

    //Prints
    private void PrintPrereqs()
    {
        String tempPrereqs = "";

        if (!myCourse.getPreRequisites().isEmpty())
        {
            for (int i = 0; i < myCourse.getPreRequisites().size(); i++)
            {
                tempPrereqs = tempPrereqs + myCourse.getPreRequisites().get(i).getCourseName() + ", ";
            }

            tempPrereqs = tempPrereqs.substring(0, tempPrereqs.length() - 2);
        }

        myCourse.setPrerequisitesGUIWorkAround(tempPrereqs);
    }

    //Tyler: Added as extension of PrintPrereqs
    private void updatePrerequisitesInCourse(Course myOtherCourse)
    {
        String tempPrereqs = "";

        if (!myOtherCourse.getPreRequisites().isEmpty())
        {
            for (int i = 0; i < myOtherCourse.getPreRequisites().size(); i++)
            {
                tempPrereqs = tempPrereqs + myOtherCourse.getPreRequisites().get(i).getCourseName() + ", ";
            }

            tempPrereqs = tempPrereqs.substring(0, tempPrereqs.length() - 2);
        }

        myOtherCourse.setPrerequisitesGUIWorkAround(tempPrereqs);
    }

    private void PrintConcurs()
    {
        String tempConcurs = "";

        if (!myCourse.getConcurrentCourses().isEmpty())
        {
            for (int i = 0; i < myCourse.getConcurrentCourses().size(); i++)
            {
                tempConcurs = tempConcurs + myCourse.getConcurrentCourses().get(i).getCourseName() + ", ";
            }

            if (error == true)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("A Concurrent Course Was Not Added");
                alert.setContentText("A course cannot be both a prerequisite and concurrent at the same time. "
                        + "The conflicting course was not added to the list of concurrent courses.");

                alert.showAndWait();
                error = false;

                if (tempConcurs.length() == 0)
                {
                    tempConcurs = "";
                }
                else
                {
                    tempConcurs = tempConcurs.substring(0, tempConcurs.length() - 2);
                }
            }
            else
            {
                tempConcurs = tempConcurs.substring(0, tempConcurs.length() - 2);
            }
        }

        myCourse.setConcurrentGUIWorkAround(tempConcurs);
    }

    //Tyler: Added as extension of PrintConcurs
    private void updateConcurrentsInCourse(Course myOtherCourse)
    {
        String tempConcurs = "";

        if (!myOtherCourse.getConcurrentCourses().isEmpty())
        {
            for (int i = 0; i < myOtherCourse.getConcurrentCourses().size(); i++)
            {
                tempConcurs = tempConcurs + myOtherCourse.getConcurrentCourses().get(i).getCourseName() + ", ";
            }

            if (error == true)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("A Concurrent Course Was Not Added");
                alert.setContentText("A course cannot be both a prerequisite and concurrent at the same time. "
                        + "The conflicting course was not added to the list of concurrent courses.");

                alert.showAndWait();
                error = false;

                if (tempConcurs.length() == 0)
                {
                    tempConcurs = "";
                }
                else
                {
                    tempConcurs = tempConcurs.substring(0, tempConcurs.length() - 2);
                }
            }
            else
            {
                tempConcurs = tempConcurs.substring(0, tempConcurs.length() - 2);
            }
        }

        myOtherCourse.setConcurrentGUIWorkAround(tempConcurs);
    }

    //Tyler: Added to change text fields on table selection.
    @FXML
    private void updateFieldsOnSelection()
    {
        for (Course courseInMainList : mainList.getMyRawCourseList())
        {
            if (!courseInMainList.equals(myCourse))
            {
                if (courseInMainList.getCourseName().toLowerCase().equals(myCourse.getCourseName().toLowerCase()))
                {
                    Alert myErrorPrompt = new Alert(AlertType.ERROR);
                    myErrorPrompt.getDialogPane().getChildren().stream()
                        .filter(node -> node instanceof Label).forEach(node -> 
                                ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    myErrorPrompt.setTitle("Course Conflict");
                    myErrorPrompt.setHeaderText("Course Name Conflict");
                    myErrorPrompt.setContentText(String.format("There already "
                            + "exists a version of %s in the main list", myCourse.getCourseName()));
                    myErrorPrompt.showAndWait();

                    CourseName.requestFocus();
                    mainTable.getSelectionModel().clearAndSelect(mainList.getMyRawCourseList().indexOf(myCourse));
                    isInErrorState = true;
                    return;
                }
            }
        }
        isInErrorState = false;

        if (mainTable.getSelectionModel().getSelectedItem() == null)
        {
            mainTable.getSelectionModel().clearSelection();
            mainTable.requestFocus();
            myPastSelection = "";
            return;
        }
        else if (myPastSelection.equals(mainTable.getSelectionModel().getSelectedItem().getCourseName()))
        {
            mainTable.getSelectionModel().clearSelection();
            mainTable.requestFocus();
            myPastSelection = "";
            myCourse = new Course();

            CourseName.clear();
            Units.clear();
            CurrentPrereqs.clear();
            CurrentConcurs.clear();
            PrereqCheck.setSelected(false);
            Prereqs.setDisable(true);
            AddPrereq.setDisable(true);
            RemovePrereq.setDisable(true);
            prereqList.setDisable(true);
            ConcurCheck.setSelected(false);
            Concurs.setDisable(true);
            AddConcur.setDisable(true);
            RemoveConcur.setDisable(true);
            concurList.setDisable(true);
            Summer.setSelected(false);
            BtnDel.setDisable(true);
            updateComboBoxTermExclusive();
            return;
        }
        CourseName.clear();
        Units.clear();
        CurrentPrereqs.clear();
        CurrentConcurs.clear();
        PrereqCheck.setSelected(false);
        Prereqs.setDisable(true);
        AddPrereq.setDisable(true);
        RemovePrereq.setDisable(true);
        prereqList.setDisable(true);
        ConcurCheck.setSelected(false);
        Concurs.setDisable(true);
        AddConcur.setDisable(true);
        RemoveConcur.setDisable(true);
        concurList.setDisable(true);
        Summer.setSelected(false);

        myCourse = mainTable.getSelectionModel().getSelectedItem();
        mainTable.requestFocus();
        myPastSelection = myCourse.getCourseName();

        CourseName.setText(myCourse.getCourseName());
        Units.setText(Double.toString(myCourse.getCourseUnits()));

        if (!myCourse.getPreRequisites().isEmpty())
        {
            for (int i = 0; i < myCourse.getPreRequisites().size(); i++)
            {
                CurrentPrereqs.add(myCourse.getPreRequisites().get(i).getCourseName());
            }

            PrereqCheck.setSelected(true);
            Prereqs.setDisable(false);
            AddPrereq.setDisable(false);
            RemovePrereq.setDisable(false);
            prereqList.setDisable(false);
            //PrereqCheckClick(event);
        }

        if (!myCourse.getConcurrentCourses().isEmpty())
        {
            for (int j = 0; j < myCourse.getConcurrentCourses().size(); j++)
            {
                CurrentConcurs.add(myCourse.getConcurrentCourses().get(j).getCourseName());
            }

            ConcurCheck.setSelected(true);
            Concurs.setDisable(false);
            AddConcur.setDisable(false);
            RemoveConcur.setDisable(false);
            concurList.setDisable(false);
            //ConcurCheckClick(event);
        }

        Summer.setSelected(myCourse.isSummerCompatible());
        BtnDel.setDisable(false);
        setNewAvailable();
        updateComboBoxTermExclusive();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        ObservableList<String> options = 
            FXCollections.observableArrayList(
                "1");
        Prereqs.setItems(AvailableCourses);
        Concurs.setItems(AvailableCourses);
        ComboBoxTermExclusive.setItems(options);
        ComboBoxTermExclusive.setDisable(true);

        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        unitsCol.setCellValueFactory(new PropertyValueFactory<>("courseUnits"));
        prerequsiteCol.setCellValueFactory(new PropertyValueFactory<>("prerequisitesGUIWorkAround"));
        concurrentCol.setCellValueFactory(new PropertyValueFactory<>("concurrentGUIWorkAround"));
        summerCol.setCellValueFactory(new PropertyValueFactory<>("summerCompatibleGUIWorkAround"));
        tblColTermExclusive.setCellValueFactory(new PropertyValueFactory<>("termExclusiveGUIWorkAround"));
        //txtFldAmountOfTerms.setDisable(true);
        txtFldSummerTermLimit.setDisable(true);

        //Tyler: Used Uluk Biy's answer to make the textfield only
        //accept decimals:
        //http://stackoverflow.com/questions/31039449/java-8-u40-textformatter-javafx-to-restrict-user-input-only-for-decimal-number
        DecimalFormat format = new DecimalFormat( "#.0" );
        
        UnitLimit.setTextFormatter( new TextFormatter<>(c ->
        {
            if ( c.getControlNewText().isEmpty() )
            {
                return c;
            }

            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = format.parse( c.getControlNewText(), parsePosition );

            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
            {
                return null;
            }
            else
            {
                return c;
            }
        }));
        
        //Tyler: Used Uluk Biy's answer to make the textfield only
        //accept decimals:
        //http://stackoverflow.com/questions/31039449/java-8-u40-textformatter-javafx-to-restrict-user-input-only-for-decimal-number
        Units.setTextFormatter( new TextFormatter<>(c ->
        {
            if ( c.getControlNewText().isEmpty() )
            {
                return c;
            }

            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = format.parse( c.getControlNewText(), parsePosition );

            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
            {
                return null;
            }
            else
            {
                return c;
            }
        }));
        
        //Tyler: Used Uluk Biy's answer to make the textfield only
        //accept decimals:
        //http://stackoverflow.com/questions/31039449/java-8-u40-textformatter-javafx-to-restrict-user-input-only-for-decimal-number
        txtFldSummerTermLimit.setTextFormatter( new TextFormatter<>(c ->
        {
            if ( c.getControlNewText().isEmpty() )
            {
                return c;
            }

            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = format.parse( c.getControlNewText(), parsePosition );

            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
            {
                return null;
            }
            else
            {
                return c;
            }
        }));
        
        //Tyler: Used Evan Knowles' Answer as reference:
        //http://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
        txtFldAmountOfTerms.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtFldAmountOfTerms.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    //Tyler: Added to change course name in real time.
    @FXML
    private void setCourseNameOnKeyPressed(KeyEvent myKeyPressed)
    {
        try
        {
            myCourse.setCourseName(CourseName.getText());
        }
        catch(Exception invalidCourseName)
        {
            //Do nothing
        }

        for (int i = 0; i < mainList.getMyRawCourseList().size(); i++)
        {
            Course myTempCourse = mainList.getMyRawCourseList().get(i);
            updateConcurrentsInCourse(myTempCourse);
            updatePrerequisitesInCourse(myTempCourse);
        }
        //Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
        setNewAvailable();
        hasUnsavedData = true;
    }

    //Tyler: Added for conveinience somewhere in this code.
    private void emptyTxtFields()
    {
        mainTable.getSelectionModel().clearSelection();
        mainTable.requestFocus();
        myPastSelection = "";
        myCourse = new Course();

        CourseName.clear();
        Units.clear();
        CurrentPrereqs.clear();
        CurrentConcurs.clear();
        PrereqCheck.setSelected(false);
        Prereqs.setDisable(true);
        AddPrereq.setDisable(true);
        RemovePrereq.setDisable(true);
        prereqList.setDisable(true);
        ConcurCheck.setSelected(false);
        Concurs.setDisable(true);
        AddConcur.setDisable(true);
        RemoveConcur.setDisable(true);
        concurList.setDisable(true);
        Summer.setSelected(false);
        BtnDel.setDisable(true);
        ComboBoxTermExclusive.getItems().clear();
        ComboBoxTermExclusive.setDisable(true);
        listViewTermExclusive.getItems().clear();
        checkBoxToggleTermExclusive.setSelected(false);
        btnAddTermExclusive.setDisable(true);
        btnRemoveTermExclusive.setDisable(true);
    }

    //Tyler: Added these functions to make the menu buttons work.
    @FXML
    private void mnuItemNew_Click(ActionEvent userInteraction)
    {
        if (!hasUnsavedData)
        {
            mainList = new RawCourseList();
            emptyTxtFields();
        }
        else
        {
            try
            {
                promptUserToSaveChanges();
            }
            catch (Exception userCanceled)
            {
                return;
            }

            mainList = new RawCourseList();
            emptyTxtFields();
        }
        TermType.clear();
        UnitLimit.clear();
        txtFldAmountOfTerms.clear();
        txtFldSummerTermLimit.clear();

        myCourse = new Course();
        mainTable.getItems().clear();
        BtnProcess.setDisable(true);
        //Used Robert Martin's answer for changing the title of the current
        //window:
        //http://stackoverflow.com/questions/13246211/javafx-how-to-get-stage-from-controller-during-initialization
        Stage myStage = (Stage) myWindow.getScene().getWindow();
        myStage.setTitle("Untitled - College Length Estimator");

        hasUnsavedData = false;
        usersInputFileName = "";
    }

    @FXML
    private void mnuItemOpen_Click(ActionEvent userInteraction)
    {
        if (hasUnsavedData)
        {
            try
            {
                promptUserToSaveChanges();
            }
            catch (Exception userCanceled)
            {
                return;
            }
        }

        FileChooser fileSeeker = new FileChooser();
        fileSeeker.setTitle("Select file to open");
        File usersFile = fileSeeker.showOpenDialog(null);

        if (usersFile == null)
        {
            return;
        }
        usersInputFileName = usersFile.getAbsolutePath();
        mainList = new RawCourseList();
        myCourse = new Course();
        readFromFile(usersInputFileName, mainList);
        setNewAvailable();
        RefreshTable();
        emptyTxtFields();
        myPastSelection = "";
    }

    @FXML
    private void mnuItemSave_Click(ActionEvent userInteraction)
    {
        if (isInErrorState)
        {
            Alert myErrorAlert = new Alert(AlertType.ERROR);
            myErrorAlert.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label).forEach(node -> 
                        ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            myErrorAlert.setTitle("Course Logic Error");
            myErrorAlert.setHeaderText("Invalid Course Logic");
            myErrorAlert.setContentText("There exists an error in the course list."
                    + " Fix this to save.");
            myErrorAlert.showAndWait();
            return;
        }

        if (usersInputFileName.equals(""))
        {
            FileChooser fileSeeker = new FileChooser();
            fileSeeker.setTitle("Enter file name");
            fileSeeker.getExtensionFilters().addAll(
                    new ExtensionFilter("Data Files", "*.dat"),
                    new ExtensionFilter("All Files", "*.*"));
            fileSeeker.setInitialFileName("myCollegePlan.dat");
            File usersFile = fileSeeker.showSaveDialog(null);

            if (usersFile == null)
            {
                return;
            }
            usersInputFileName = usersFile.getAbsolutePath();
            saveToFile(usersInputFileName, mainList);
        }
        else
        {
            saveToFile(usersInputFileName, mainList);
        }
    }

    @FXML
    private void mnuItemSaveAs_Click(ActionEvent userInteraction)
    {
        if (isInErrorState)
        {
            Alert myErrorAlert = new Alert(AlertType.ERROR);
            myErrorAlert.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label).forEach(node -> 
                        ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            myErrorAlert.setTitle("Course Logic Error");
            myErrorAlert.setHeaderText("Invalid Course Logic");
            myErrorAlert.setContentText("There exists an error in the course list."
                    + " Fix this to save.");
            myErrorAlert.showAndWait();
            return;
        }

        FileChooser fileSeeker = new FileChooser();
        fileSeeker.setTitle("Enter file name");
        fileSeeker.getExtensionFilters().addAll(
                new ExtensionFilter("Data Files", "*.dat"),
                new ExtensionFilter("All Files", "*.*"));
        fileSeeker.setInitialFileName("myCollegePlan.dat");
        File usersFile = fileSeeker.showSaveDialog(null);

        if (usersFile == null)
        {
            return;
        }
        usersInputFileName = usersFile.getAbsolutePath();
        saveToFile(usersInputFileName, mainList);
    }

    @FXML
    private void mnuItemClose_Click(ActionEvent userInteraction)
    {
        if (hasUnsavedData)
        {
            try
            {
                promptUserToSaveChanges();
            }
            catch (Exception userCanceled)
            {
                return;
            }
        }
        Platform.exit();
    }

    //Tyler: Added to check for one way of detecting changes.
    @FXML
    private void onKeyInput()
    {
        hasUnsavedData = true;
    }

    //Tyler: Added to save current contents of what is in mainlist to a file.
    private void saveToFile(String myFile, RawCourseList myRawCourseList)
    {
        String osLineSeperator = System.getProperty("line.separator");
        try
        {
            FileWriter fileCreator = new FileWriter(new File(myFile));

            String termInformationLine = "";
            termInformationLine += TermType.getText() + "|";
            termInformationLine += UnitLimit.getText() + "|";
            termInformationLine += txtFldAmountOfTerms.getText() + "|";
            termInformationLine += txtFldSummerTermLimit.getText() + osLineSeperator;

            fileCreator.write(termInformationLine);

            for (Course myCourseInList : myRawCourseList.getMyRawCourseList())
            {
                fileCreator.write(myCourseInList.toString());
            }
            fileCreator.close();
            hasUnsavedData = false;

            //Used Robert Martin's answer for changing the title of the current
            //window:
            //http://stackoverflow.com/questions/13246211/javafx-how-to-get-stage-from-controller-during-initialization
            Stage myStage = (Stage) myWindow.getScene().getWindow();
            myStage.setTitle(new File(myFile).getName() + " - College Length Estimator");
        }
        catch (IOException fileAccessDenied)
        {
            Alert myErrorAlert = new Alert(Alert.AlertType.ERROR);
            myErrorAlert.setTitle("File Reading Error");
            myErrorAlert.setContentText("File Reading Error: " + fileAccessDenied.getMessage());
            myErrorAlert.showAndWait();
            usersInputFileName = "";
        }
    }

    //Tyler: Added to use if the user has unsaved changes.
    private void promptUserToSaveChanges() throws Exception
    {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Save changes before exit?");
        alert.setHeaderText(null);
        alert.setContentText("You have unsaved data. Do you want to save?");

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

        Optional<ButtonType> userChoice = alert.showAndWait();

        if (userChoice.get() == ButtonType.CANCEL)
        {
            throw new Exception("User canceled.");
        }
        else if (userChoice.get() == ButtonType.NO || userChoice.get() == null)
        {
            //Do nothing
        }
        else
        {
            if (isInErrorState)
            {
                Alert myErrorAlert = new Alert(AlertType.ERROR);
                myErrorAlert.getDialogPane().getChildren().stream()
                    .filter(node -> node instanceof Label).forEach(node -> 
                            ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                myErrorAlert.setTitle("Course Logic Error");
                myErrorAlert.setHeaderText("Invalid Course Logic");
                myErrorAlert.setContentText("There exists an error in the course list."
                        + " Fix this to save.");
                myErrorAlert.showAndWait();
                throw new Exception("There exists an error in the course list."
                        + " Fix this to save.");
            }

            if (usersInputFileName.equals(""))
            {
                FileChooser fileSeeker = new FileChooser();
                fileSeeker.setTitle("Enter file name");
                fileSeeker.getExtensionFilters().addAll(
                        new ExtensionFilter("Data Files", "*.dat"),
                        new ExtensionFilter("All Files", "*.*"));
                fileSeeker.setInitialFileName("myCollegePlan.dat");
                File usersFile = fileSeeker.showSaveDialog(null);

                if (usersFile == null)
                {
                    return;
                }
                usersInputFileName = usersFile.getAbsolutePath();
                saveToFile(usersInputFileName, mainList);
            }
            else
            {
                saveToFile(usersInputFileName, mainList);
            }
        }
    }

    //Tyler: Added to read contents from file processed by (or has similar processed
    //contents of) the function saveToFile
    private void readFromFile(String myFile, RawCourseList myRawCourseList)
    {
        Scanner inputFile = null;
        String courseInformationFromLine[] = null;
        File usersFile = new File(myFile);
        RawCourseList myNewRawCourseList = new RawCourseList();
        ArrayList<String[]> coursePrerequisites = new ArrayList<>();
        ArrayList<String[]> courseConcurrents = new ArrayList<>();
        ArrayList<String> coursesWithPrerequisites = new ArrayList<>();
        ArrayList<String> coursesWithConcurrents = new ArrayList<>();

        try
        {
            inputFile = new Scanner(usersFile);
        }
        catch (FileNotFoundException fileNotFound)
        {
            Alert myErrorAlert = new Alert(Alert.AlertType.ERROR);
            myErrorAlert.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label).forEach(node -> 
                        ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            myErrorAlert.setTitle("File Reading Error");
            myErrorAlert.setContentText("File Reading Error: " + fileNotFound.getMessage());
            myErrorAlert.showAndWait();
            usersInputFileName = "";
            return;
        }

        if (!inputFile.hasNextLine())
        {
            Alert myErrorAlert = new Alert(Alert.AlertType.ERROR);
            myErrorAlert.setTitle("File Reading Error");
            myErrorAlert.setContentText("File not opened: Empty file");
            myErrorAlert.showAndWait();
            usersInputFileName = "";
            return;
        }

        String myTermInfoLine = inputFile.nextLine();
        String myTermInfoArray[] = myTermInfoLine.split("\\|", 4);

        if (myTermInfoArray.length != 4)
        {
            Alert myErrorAlert = new Alert(Alert.AlertType.ERROR);
            myErrorAlert.setTitle("File Reading Error");
            myErrorAlert.setContentText("File reading error: Invalid or corrupt contents");
            myErrorAlert.showAndWait();
            usersInputFileName = "";
            return;
        }

        TermType.setText(myTermInfoArray[0]);
        UnitLimit.setText(myTermInfoArray[1]);

        if (myTermInfoArray[2].equals(""))
        {
            txtFldAmountOfTerms.setText("");
            //txtFldAmountOfTerms.setDisable(true);
        }
        else
        {
            txtFldAmountOfTerms.setText(myTermInfoArray[2]);
            txtFldAmountOfTerms.setDisable(false);
        }

        if (myTermInfoArray[3].equals(""))
        {
            txtFldSummerTermLimit.setText("");
            txtFldSummerTermLimit.setDisable(true);
        }
        else
        {
            txtFldSummerTermLimit.setText(myTermInfoArray[3]);
            txtFldSummerTermLimit.setDisable(false);
        }

        while (inputFile.hasNextLine())
        {
            String myInputLine = inputFile.nextLine().replace("Course Name:", "")
                    .replace("Course Units:", "").replace("Prerequisites:", "")
                    .replace("Concurrents:", "").replace("Summer Compatible:", "")
                    .replace("Term Exclusives:", "");
            courseInformationFromLine = myInputLine.split("\\|");

            try
            {
                Course myCourseRead = new Course(courseInformationFromLine[0], Double.parseDouble(courseInformationFromLine[1]));

                if (!courseInformationFromLine[2].equals(""))
                {
                    String prerequisitesForThisCourse[] = courseInformationFromLine[2].split(",");
                    coursesWithPrerequisites.add(courseInformationFromLine[0]);
                    coursePrerequisites.add(prerequisitesForThisCourse);
                }

                if (!courseInformationFromLine[3].equals(""))
                {
                    String concurrentsForThisCourse[] = courseInformationFromLine[3].split(",");
                    coursesWithConcurrents.add(courseInformationFromLine[0]);
                    courseConcurrents.add(concurrentsForThisCourse);
                }

                myCourseRead.setSummerCompatible(Boolean.parseBoolean(courseInformationFromLine[4]));
                
                if(courseInformationFromLine.length == 6 && 
                        !courseInformationFromLine[5].equals(""))
                {
                    String termExclusivesForThisCourse[] = courseInformationFromLine[5].split(",");
                    
                    for(String myTermExclusiveString : termExclusivesForThisCourse)
                    {
                        if(!myCourseRead.getTermExclusiveIdentifiers().
                                contains(Integer.parseInt(myTermExclusiveString))
                                && Integer.parseInt(myTermExclusiveString) != 0)
                        {
                            myCourseRead.getTermExclusiveIdentifiers().add(
                                Integer.parseInt(myTermExclusiveString));
                        }
                    }
                    if(!myCourseRead.getTermExclusiveIdentifiers().isEmpty()) 
                    {
                        myCourseRead.setTermExclusiveGUIWorkAround("Yes");
                    }
                    else 
                    {
                        myCourseRead.setTermExclusiveGUIWorkAround("No");
                    }
                }
                else
                {
                    myCourseRead.setTermExclusiveGUIWorkAround("No");
                }
                myNewRawCourseList.addToList(myCourseRead);
            }
            catch (Exception interpretiveException)
            {
                if (interpretiveException.getMessage().equals("Duplicate Found. Omitting.."))
                {
                    System.out.println(interpretiveException.getMessage());
                }
                else
                {
                    Alert myErrorAlert = new Alert(Alert.AlertType.ERROR);
                    myErrorAlert.getDialogPane().getChildren().stream()
                        .filter(node -> node instanceof Label).forEach(node -> 
                                ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    myErrorAlert.setTitle("File Reading Error");
                    myErrorAlert.setContentText("File Reading Error: " + interpretiveException.getMessage());
                    myErrorAlert.showAndWait();
                    
                    TermType.clear();
                    UnitLimit.clear();
                    txtFldAmountOfTerms.clear();
                    txtFldSummerTermLimit.clear();
                    txtFldSummerTermLimit.setDisable(true);
                    usersInputFileName = "";
                    //Used Robert Martin's answer for changing the title of the current
                    //window:
                    //http://stackoverflow.com/questions/13246211/javafx-how-to-get-stage-from-controller-during-initialization
                    Stage myStage = (Stage) myWindow.getScene().getWindow();
                    myStage.setTitle("Untitled - College Length Estimator");
                    return;
                }
            }
        }

        for (Course myCurrentCourse : myNewRawCourseList.getMyRawCourseList())
        {
            if (coursesWithPrerequisites.indexOf(myCurrentCourse.getCourseName()) != -1
                    && myCurrentCourse.getCourseName().equals(coursesWithPrerequisites.
                            get(coursesWithPrerequisites.indexOf(myCurrentCourse.getCourseName()))))
            {
                for (String myCourseNameInList : coursePrerequisites.get(0))
                {
                    try
                    {
                        myCurrentCourse.addPrerequisite(myNewRawCourseList.getCourseByName(myCourseNameInList));
                    }
                    catch (Exception courseNotFound)
                    {
                        Alert myErrorAlert = new Alert(Alert.AlertType.ERROR);
                        myErrorAlert.getDialogPane().getChildren().stream()
                            .filter(node -> node instanceof Label).forEach(node -> 
                                    ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                        myErrorAlert.setTitle("File Reading Error");
                        myErrorAlert.setContentText("File Reading Error: " + courseNotFound.getMessage());
                        
                        TermType.clear();
                        UnitLimit.clear();
                        txtFldAmountOfTerms.clear();
                        //txtFldAmountOfTerms.setDisable(true);
                        txtFldSummerTermLimit.clear();
                        txtFldSummerTermLimit.setDisable(true);
                        myErrorAlert.showAndWait();
                        usersInputFileName = "";
                        //Used Robert Martin's answer for changing the title of the current
                        //window:
                        //http://stackoverflow.com/questions/13246211/javafx-how-to-get-stage-from-controller-during-initialization
                        Stage myStage = (Stage) myWindow.getScene().getWindow();
                        myStage.setTitle("Untitled - College Length Estimator");
                        return;
                    }
                }
                coursesWithPrerequisites.remove(myCurrentCourse.getCourseName());
                coursePrerequisites.remove(0);
            }

            if (coursesWithConcurrents.indexOf(myCurrentCourse.getCourseName()) != -1
                    && myCurrentCourse.getCourseName().equals(coursesWithConcurrents.
                            get(coursesWithConcurrents.indexOf(myCurrentCourse.getCourseName()))))
            {
                for (String myCourseNameInList : courseConcurrents.get(0))
                {
                    try
                    {
                        myCurrentCourse.addConcurrent(myNewRawCourseList.getCourseByName(myCourseNameInList));
                    }
                    catch (Exception interpretiveError)
                    {
                        if(interpretiveError.getMessage().equals(String.format("%s is currently in %s's concurrents."
                            , myCourseNameInList, myCurrentCourse.getCourseName())))
                        {
                            //Do nothing
                        }
                        else
                        {
                            Alert myErrorAlert = new Alert(Alert.AlertType.ERROR);
                            myErrorAlert.getDialogPane().getChildren().stream()
                                .filter(node -> node instanceof Label).forEach(node -> 
                                        ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                            myErrorAlert.setTitle("File Reading Error");
                            myErrorAlert.setContentText("File Reading Error: " + interpretiveError.getMessage());
                            TermType.clear();
                            UnitLimit.clear();
                            txtFldAmountOfTerms.clear();
                            //txtFldAmountOfTerms.setDisable(true);
                            txtFldSummerTermLimit.clear();
                            txtFldSummerTermLimit.setDisable(true);
                            myErrorAlert.showAndWait();
                            usersInputFileName = "";
                            //Used Robert Martin's answer for changing the title of the current
                            //window:
                            //http://stackoverflow.com/questions/13246211/javafx-how-to-get-stage-from-controller-during-initialization
                            Stage myStage = (Stage) myWindow.getScene().getWindow();
                            myStage.setTitle("Untitled - College Length Estimator");
                            return;
                        }
                    }
                }
                coursesWithConcurrents.remove(myCurrentCourse.getCourseName());
                courseConcurrents.remove(0);
            }
        }

        for (Course courseInMyNewRawCourseList : myNewRawCourseList.getMyRawCourseList())
        {
            try
            {
                myRawCourseList.addToList(courseInMyNewRawCourseList);
                myCourse = courseInMyNewRawCourseList;
                PrintConcurs();
                PrintPrereqs();

                if (myCourse.isSummerCompatible())
                {
                    myCourse.setSummerCompatibleGUIWorkAround("Yes");
                    txtFldAmountOfTerms.setDisable(false);
                    txtFldSummerTermLimit.setDisable(false);
                }
                else
                {
                    myCourse.setSummerCompatibleGUIWorkAround("No");
                }
            }
            catch (Exception alreadyInRawCourseList)
            {
                System.out.println(alreadyInRawCourseList.getMessage());
            }
        }
        concurList.setItems(CurrentConcurs);
        prereqList.setItems(CurrentPrereqs);
        makeSummerFieldsEditableIfSummerCoursesExist();
        myCourse = new Course();
        BtnProcess.setDisable(false);
        hasUnsavedData = false;

        //Used Robert Martin's answer for changing the title of the current
        //window:
        //http://stackoverflow.com/questions/13246211/javafx-how-to-get-stage-from-controller-during-initialization
        Stage myStage = (Stage) myWindow.getScene().getWindow();
        myStage.setTitle(usersFile.getName() + " - College Length Estimator");
    }

    //Tyler: Added to check for a user closing out of the application.
    //Used T-and-M Mike's answer from the following source as reference:
    //http://stackoverflow.com/questions/13727314/prevent-or-cancel-exit-javafx-2
    public boolean shutdownWithoutSavingCheck()
    {
        if (hasUnsavedData)
        {
            try
            {
                promptUserToSaveChanges();
            }
            catch (Exception userCanceled)
            {
                return false;
            }
            return true;
        }
        return true;
    }

    @FXML
    private void mnuItemAboutClick()
    {
        Alert myAboutPage = new Alert(AlertType.INFORMATION);
        myAboutPage.getDialogPane().getChildren().stream()
            .filter(node -> node instanceof Label).forEach(node -> 
                    ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        myAboutPage.setTitle("About");
        myAboutPage.setHeaderText(null);
        myAboutPage.setContentText("The College Length Estimator is a Java application"
                + " that provides an estimate of how long it would take to complete"
                + " a set number of courses based on the course information, "
                + "term information, and limits given.\n\nAuthors:\nTyler Schmidt"
                + " - Course Processing Logic/GUI Tweaks\nJesse Paone"
                + " - GUI Design");
        myAboutPage.showAndWait();
    }
    
    @FXML
    private void amountOfTermsUpdateComboBoxPrompt(KeyEvent userInput)
    {
        boolean hasTermExclusiveCourseInList = false;
        for(Course myCourseInRawCourseList : mainList.getMyRawCourseList())
        {
            if(!myCourseInRawCourseList.getTermExclusiveIdentifiers().isEmpty())
            {
                hasTermExclusiveCourseInList = true;
                break;
            }
        }
        
        if(hasTermExclusiveCourseInList)
        {
            Alert amountOfTermsChangeWarning = new Alert(AlertType.CONFIRMATION);
            amountOfTermsChangeWarning.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label).forEach(node -> 
                        ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            amountOfTermsChangeWarning.setTitle("Term Amount Change Warning");
            amountOfTermsChangeWarning.setHeaderText("Term Amount Change Confirmation");
            amountOfTermsChangeWarning.setContentText("Changing this will reset"
                    + " all courses back to being non-term exclusive. Are you"
                    + " sure you want to do this?");
            
            Optional<ButtonType> userResponse = amountOfTermsChangeWarning.showAndWait();
            
            if(userResponse.get() == ButtonType.OK)
            {
                int myPastMyCourseLocation = mainList.getMyRawCourseList().indexOf(myCourse);
                
                for(Course myCourseInRawCourseList : mainList.getMyRawCourseList())
                {
                    myCourse = myCourseInRawCourseList;
                    myCourse.getTermExclusiveIdentifiers().clear();
                    updateComboBoxTermExclusive();
                }
                if(myPastMyCourseLocation != -1)
                {
                    myCourse = mainList.getMyRawCourseList().get(myPastMyCourseLocation);
                }
                else
                {
                    myCourse = new Course();
                }
            }
            else
            {
                userInput.consume();
            }
        }
        else
        {
            updateComboBoxTermExclusive();
        }
    }
    
    @FXML
    private void updateComboBoxTermExclusive()
    {
        if(!myCourse.getTermExclusiveIdentifiers().isEmpty() && !(txtFldAmountOfTerms.getText().equals("0") || txtFldAmountOfTerms.getText().equals("")))
        {
            ComboBoxTermExclusive.setDisable(false);
            checkBoxToggleTermExclusive.setSelected(true);
            btnAddTermExclusive.setDisable(false);
            btnRemoveTermExclusive.setDisable(false);
            listViewTermExclusive.setDisable(false);
            ComboBoxTermExclusive.getItems().clear();
            listViewTermExclusive.getItems().clear();
            
            int amountOfTerms = Integer.parseInt(txtFldAmountOfTerms.getText());
            if(myCourse.isSummerCompatible())
            {
                amountOfTerms++;
            }
            
            for(int i = 1; i <= amountOfTerms; i++)
            {
                ComboBoxTermExclusive.getItems().add(i);
            }
            
            for(Integer myCourseTermExclusive : myCourse.getTermExclusiveIdentifiers())
            {
                listViewTermExclusive.getItems().add(myCourseTermExclusive);
            }
            myCourse.setTermExclusiveGUIWorkAround("Yes");
        }
        else
        {
            ComboBoxTermExclusive.getItems().clear();
            ComboBoxTermExclusive.setDisable(true);
            checkBoxToggleTermExclusive.setSelected(false);
            btnAddTermExclusive.setDisable(true);
            btnRemoveTermExclusive.setDisable(true);
            listViewTermExclusive.getItems().clear();
            listViewTermExclusive.setDisable(true);
            
            myCourse.setTermExclusiveGUIWorkAround("No");
        }
        //Tyler: Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
    }
    
    @FXML
    private void toggleTermExclusiveComboBox()
    {
        if(checkBoxToggleTermExclusive.isSelected() && !(txtFldAmountOfTerms.getText().equals("0") || txtFldAmountOfTerms.getText().equals("")))
        {
            ComboBoxTermExclusive.setDisable(false);
            btnAddTermExclusive.setDisable(false);
            btnRemoveTermExclusive.setDisable(false);
            ComboBoxTermExclusive.getItems().clear();
            listViewTermExclusive.setDisable(false);
            
            int amountOfTerms = Integer.parseInt(txtFldAmountOfTerms.getText());
            if(Summer.isSelected())
            {
                amountOfTerms++;
            }
            
            for(int i = 1; i <= amountOfTerms; i++)
            {
                ComboBoxTermExclusive.getItems().add(i);
            }
            myCourse.setTermExclusiveGUIWorkAround("Yes");
        }
        else
        {
            checkBoxToggleTermExclusive.setSelected(false);
            ComboBoxTermExclusive.setDisable(true);
            btnAddTermExclusive.setDisable(true);
            btnRemoveTermExclusive.setDisable(true);
            myCourse.getTermExclusiveIdentifiers().clear();
            listViewTermExclusive.getItems().clear();
            listViewTermExclusive.setDisable(true);
            
            if(txtFldAmountOfTerms.getText().equals("0") || txtFldAmountOfTerms.getText().equals(""))
            {
                txtFldAmountOfTerms.requestFocus();
            }
            myCourse.setTermExclusiveGUIWorkAround("No");
        }
        
        //Tyler: Workaround used from Daniel De Leon's answer:
        //http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        mainTable.getColumns().get(0).setVisible(false);
        mainTable.getColumns().get(0).setVisible(true);
        hasUnsavedData = true;
    }
}
