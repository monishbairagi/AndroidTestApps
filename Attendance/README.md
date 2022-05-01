# Attendance App

<p align="center">
<img width="300" height="300" src="https://raw.githubusercontent.com/monishbairagi/AndroidTestApps/master/Attendance/app/src/main/res/drawable-nodpi/ic_launcher.png"/>
</p>

## About
Taking and maintaining attendance in pen and paper is quite a hectic task for teachers. So I put this same concept on the mobile application so that a teacher can take and maintain attendance inside the mobile application. And also they can export the attendance data as a `.xls` file at any time.

Download App: [Click Here](https://github.com/monishbairagi/AndroidTestApps/blob/master/APKs/attendance-app.apk)

## Features
- Import students from the `.csv` file.
- Export attendance data as a `.xls` file.
- Can take attendance of other dates.
- Able to modify a particular student's attendance status.
- Easy User Interface.

## How it works
### Add Class:
- **Step-1:** Click bottom right plus button to add a new class.
- **Step-2:** Enter subject name and class name, then enter Ok.

### Edit Class:
- **Step-1:** Long press desired class from home and one popup menu will appear.
- **Step-2:** Click `Edit` option.
- **Step-3:** Enter new subject name and class name, then enter Ok.

### Delete Class:
- **Step-1:** Long press desired class from home and one popup menu will appear.
- **Step-2:** Click `Delete` option.
- **Step-3:** Confirm the popup warning and the class will be deleted.

### Add Student:
- **Step-1:** Open desired class.
- **Step-2:** Click top-right corner three dot to open a menu.
- **Step-3:** Click `Add Student` option to add menually or you can `Import CSV` to add students from csv file instead.
- **Step-4:** If you clicked `Add Student` option then enter roll number, student name and enter ok button to add one student.

### Edit Student:
- **Step-1:** Open desired class.
- **Step-2:** Long press desired student and one popup menu will appear.
- **Step-3:** Click `Edit` option.
- **Step-4:** Enter new roll number and student name, then enter Ok.

### Delete Student:
- **Step-1:** Open desired class.
- **Step-2:** Long press desired student and one popup menu will appear.
- **Step-3:** Click `Delete` option.
- **Step-4:** Confirm the popup warning and the student will be deleted.

### Take Attendance:
- **Step-1:** Open desired class.
- **Step-2:** Single click a student to make him/her present and double click to make him/her absent. Green color indicates present and Red color indicates absent.
- **Step-3:** By default you will take todays attendance. You can also change date by clicking top-right corner three dot button and click `Change Date` option.
- **Step-4:** Click top-right save button to save attendance data into database.

### Edit Attendance:
- **Step-1:** Open desired class.
- **Step-2:** Single click to toggle existing attendance of a student. Green color indicates present and Red color indicates absent.
- **Step-3:** Click top-right save button to save attendance data into database.

### See Records:
- **Step-1:** Open `Home` page of the app.
- **Step-2:** Click bottom-right `Download` button.
- **Step-3:** Click desired class.
- **Step-4:** Click desired Month. Now you can see whole attendance sheet.

### Download Records:
- **Step-1:** Open `Home` page of the app.
- **Step-2:** Click bottom-right `Download` button.
- **Step-3:** Click desired class.
- **Step-4:** Click desired Month.
- **Step-5:** Click top-right corner download button.
- **Step-6:** Confirm the popup warning and attendance sheet will be downloaded to `Internal/Attendance App Data` folder.

## Technology Used
- Android Studio » Java
- SQLite Databse

## Library Used
- [OpenCSV » 4.6](https://mvnrepository.com/artifact/com.opencsv/opencsv/4.6)
- [Apache POI Common » 3.17](https://mvnrepository.com/artifact/org.apache.poi/poi/3.17)

## Contact
- [Email](mailto:monishbairagi1999@gmail.com)
- [LinkedIn](https://in.linkedin.com/in/monishkumarbairagi)
