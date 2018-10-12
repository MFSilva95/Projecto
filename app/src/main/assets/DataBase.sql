CREATE TABLE IF NOT EXISTS UserInfo (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, DType TEXT NOT NULL, InsulinRatio INTEGER NOT NULL, CarbsRatio INTEGER NOT NULL, LowerRange REAL NOT NULL, HigherRange REAL NOT NULL, BDate DATETIME, Gender TEXT, Height REAL, DateTimeUpdate DATETIME NOT NULL, BG_Target REAL);
CREATE TABLE IF NOT EXISTS Note (Id INTEGER PRIMARY KEY AUTOINCREMENT, Note TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS Reg_Weight (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, DateTime DATETIME NOT NULL, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS Reg_A1c (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, DateTime DATETIME NOT NULL, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS Reg_Cholesterol (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, DateTime DATETIME NOT NULL, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS Medicine (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, Units TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS Disease (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name Text NOT NULL);
CREATE TABLE IF NOT EXISTS Tag (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name Text NOT NULL, TimeStart DATETIME, TimeEnd DATETIME);
CREATE TABLE IF NOT EXISTS Reg_Medication (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Id_Medicine INTEGER NOT NULL, Id_Disease INTEGER NOT NULL, Value REAL NOT NULL, DateTime DATETIME NOT NULL, LastUpdate DATETIME, Id_Tag INTEGER, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Medicine) REFERENCES Medicine(Id), FOREIGN KEY(Id_Disease) REFERENCES Disease(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS Reg_Disease (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Disease TEXT NOT NULL, StartDate DATETIME NOT NULL, EndDate DATETIME, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS Reg_BloodPressure (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Systolic INTEGER NOT NULL, Diastolic INTEGER NOT NULL, DateTime DATETIME NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS Exercise (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name Text NOT NULL);
CREATE TABLE IF NOT EXISTS Reg_Exercise (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Exercise INTEGER NOT NULL, Duration INTEGER NOT NULL, Effort INTEGER NOT NULL, StartDateTime DATETIME NOT NULL, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS Reg_BloodGlucose (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, DateTime DATETIME NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, Target_BG REAL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS Insulin (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, Type TEXT, Action TEXT, Duration REAL);
CREATE TABLE IF NOT EXISTS Reg_CarboHydrate (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, PhotoPath TEXT, DateTime DATETIME NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, Id_Meal INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS Reg_Insulin (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Id_Insulin INTEGER NOT NULL, Id_BloodGlucose INTEGER, DateTime DATETIME NOT NULL, Target_BG REAL, Value REAL NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Insulin) REFERENCES Insulin(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY(Id_BloodGlucose) REFERENCES Reg_BloodGlucose(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);
CREATE TABLE IF NOT EXISTS BG_Target (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name Text NOT NULL, TimeStart DATETIME, TimeEnd DATETIME, Value REAL);
CREATE TABLE IF NOT EXISTS Feature (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, Activated BOOLEAN NOT NULL DEFAULT FALSE);
CREATE TABLE IF NOT EXISTS Sync_Images_Diff (FileName TEXT PRIMARY KEY);
CREATE TABLE IF NOT EXISTS Db_Info (Version TEXT NOT NULL, DateTime DATETIME NOT NULL);
CREATE TABLE IF NOT EXISTS Badges (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, DateTime DATETIME NOT NULL, Type TEXT NOT NULL, Name TEXT NOT NULL, Medal TEXT NOT NULL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));
CREATE TABLE IF NOT EXISTS Points (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, DateTime DATETIME NOT NULL, Value INTEGER NOT NULL, Origin TEXT, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));
CREATE TABLE IF NOT EXISTS Activity_Log (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, DateTime DATETIME NOT NULL, Activity TEXT NOT NULL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));
CREATE TABLE IF NOT EXISTS Clicks_Log (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, DateTime DATETIME NOT NULL, Activity TEXT NOT NULL, X_Value REAL, Y_Value REAL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));
CREATE TABLE IF NOT EXISTS Sensitivity_Reg(Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, Name TEXT NOT NULL, TimeStart DATETIME NOT NULL, TimeEnd DATETIME NOT NULL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));
CREATE TABLE IF NOT EXISTS Ratio_Reg(Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, Name TEXT NOT NULL, TimeStart DATETIME NOT NULL, TimeEnd DATETIME NOT NULL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));
CREATE TABLE IF NOT EXISTS Record(Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, DateTime DATETIME NOT NULL, Id_Tag INTEGER, Id_Carbs INTEGER, Id_Insulin INTEGER, Id_BloodGlucose INTEGER, Id_Note INTEGER);

PRAGMA foreign_keys=ON;
