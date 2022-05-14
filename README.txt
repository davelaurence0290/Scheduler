======== Scheduler 1.1 ==========
Final Project for WGU Course C195
Authored by David Smith, Spring 2021.


Scheduling program to store Appointments, Customers, and their data.

The program allows the user to create, edit, and store appointments with customers. Customers and their data can also be managed, including addresses, cities, and countries.

Scheduler stores its records in an online database through JDBC functionality. As such, items created and saved are stored and can be accessed at a later time.

=================================
		DATA STRUCTURE

The data stored in the database through relations is interlinked. The structure of the objects is as follows:

User: the current user of the program.
Appointment: Core unit of the program.
	User: The user who is providing/attending this appointment with the customer.
	Customer: The customer for which the appointment is scheduled.
		Address: Address of the customer.
			City: City for the address.
				Country: The country this city is in.
				
The 'nested' nature of the objects has heavily influenced the use-cases and operation flow of the software, which will be explained in the PROGRAM FLOW section.

=================================
			LOG IN
			
The default user for the program is:
Username: test
Password: test

There is currently no way to add users through the Graphic Interface, although a DBA could add user records to the DB table 'user'.

=================================
		PROGRAM FLOW
		
	   --- MAIN PAGE ---

The main page contains a calendar view to show all scheduled appointments in the currently selected timeframe. This view can be changed between Month- and Week-view selections at the right of the page. There are also time step buttons to move forward or backward in time for these views (by months or weeks).

Manage Customers through the top menu: Edit > Manage Customers

Either through the Edit menu at the top, or buttons at bottom right, one can add, edit, or delete Appointments. This is the main functionality of the program and provides a portal to add, edit, or delete any of the underlying data objects stored in the database (See DATA STRUCTURE above).

	   --- REPORTS ---

The Generate Report button at the bottom right of the main page opens a prompt for the user to select the desired report type. The available types are:
Number of Appointment Types by Month: This report will display the count of unique (case-insensitive) Appointment Types in each month in a time span of +/- 6 months from the current, real-time month.
Appointment Schedule for User: Choose a user from the given ComboBox. Displays a table view of all FUTURE appointments for the chosen user.
Number of Appointments per Day this Week: Displays the count of appointments for each day in the current week.

  --- MANAGE APPOINTMENTS ---

This page allows the actual adding and editing of Appointment objects. As shown in DATA STRUCTURE, each DB object is composed in part a 'parent' object, forming a chain of sorts. From the MANAGE APPOINTMENTS window, one can also click the New or Edit buttons at right in the Select Customer section to open a new window to manage Customers.

To create or edit an Appointment, simply fill in the indicated required fields and ensure the desired Customer is selected at right in the Select Customer table, and click save (bottom right). Close the window with the 'Done' button.

   --- MANAGE CUSTOMERS ---

This page allows the adding and editing of Customer objects. From the MANAGE CUSTOMERS window, one can also click the New or Edit buttons at right in the Select Address section to open a new window to manage Addresses.

To create or edit a Customer, simply fill in the indicated required fields and ensure the desiredn Address is selected at right in the Select Address table, and click save (bottom right). Close the window with the 'Done' button.

   --- MANAGE ADDRESSES ---

This page allows the adding and editing of Address objects. From the MANAGE ADDRESS window, one can also click the New or Edit buttons at right in the Select City section to open a new window to manage Cities.

To create or edit a Address, simply fill in the indicated required fields and ensure the desired City is selected at right in the Select City table, and click save (bottom right). Close the window with the 'Done' button.

    --- MANAGE CITIES ---

This page allows the adding and editing of City objects. From the MANAGE CITIES window, one can also enter a NEW country in the search Countries text box and click the (+) button to add this to the list of available Countries.

To create or edit a City, simply fill in the indicated required fields and ensure the desired Country is selected at right in the Select Country table, and click save (bottom right). Close the window with the 'Done' button.

=================================
		LOCALIZATION

To demonstrate the use localization properties, the log in screen has been designed to display its text and labels in English and French. If neither language Locale is the default setting on the JVM at runtime, the system will default to English. Below are the property file names contained:
SchedulerLogIn_en.properties
SchedulerLogIn_fr.properties
SchedulerLogIn.properties

=================================
		   NOTES

While users can directly enter the Manage Appointments or Manage Customers windows through a few methods, there is currently no way to manage available Addresses, Cities, or Countries directly. To manage these objects, one must navigate through the Appointment and Customer management windows. This choice was made so that any Addresses, Cities, or Countries created would be immediately used for the current appointment or customer. This should help discourage unneeded data from entering the DB, even if not formally prevented.

Certainly, some features that are present in modern scheduling software are not present. I'm sure you will think of a few things while navigating through the windows. These are those quality of life improvements the modern user has come to expect from software that make programs very intuitive and easy to use. This software is clearly a learning project which provides the basic functionality needed to schedule and retain appointment records. 'Future Releases' would be able to improve and add such features.
