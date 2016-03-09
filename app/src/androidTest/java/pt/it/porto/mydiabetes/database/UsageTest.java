package pt.it.porto.mydiabetes.database;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import pt.it.porto.mydiabetes.ui.dataBinding.InsulinDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.utils.DateUtils;

import static org.junit.Assert.*;

public class UsageTest {


	Context mMockContext;

	@Before
	public void setUp() {
		mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
	}


	@Test
	public void testGetOldestRegist() throws Exception {
		// test if there isn't any regist
		MyDiabetesStorage storage = MyDiabetesStorage.getInstance(mMockContext);
		String date = new Usage(storage).getOldestRegist();
		Calendar calendar = Calendar.getInstance();
		Calendar lastRegist = DateUtils.parseDateTime(date);

		Assert.assertTrue(calendar.getTimeInMillis()-lastRegist.getTimeInMillis()<1000);

		DB_Write dbWrite = new DB_Write(mMockContext);

		// test if there is a old regist in the db
		dbWrite.MyData_Save(DB_WriteTest.getMockUserData());
		InsulinDataBinding insulin = new InsulinDataBinding();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);

		calendar.roll(Calendar.WEEK_OF_YEAR, false);
		InsulinRegDataBinding insulinReg = new InsulinRegDataBinding();
		insulinReg.setIdUser(1);
		insulinReg.setId(1);
		insulinReg.setIdInsulin(1);
		insulinReg.setIdTag(1);
		insulinReg.setDateTime(calendar);
		insulinReg.setInsulinUnits(3);
		insulinReg.setTargetGlycemia(100);

		dbWrite.Insulin_Save(insulinReg);

		date = new Usage(storage).getOldestRegist();
		lastRegist = DateUtils.parseDateTime(date);

		Assert.assertTrue(calendar.getTimeInMillis()-lastRegist.getTimeInMillis()<1000);
	}

	@Test
	public void testSetAppVersion() throws Exception {
		MyDiabetesStorage storage = MyDiabetesStorage.getInstance(mMockContext);

		String version="2.0";
		Cursor cursor = storage.query(MyDiabetesContract.DbInfo.TABLE_NAME, new String[]{MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION}, MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION + "=?", new String[]{version}, null, null, null);
		Assert.assertEquals(0, cursor.getCount());

		new Usage(storage).setAppVersion(version);

		cursor = storage.query(MyDiabetesContract.DbInfo.TABLE_NAME, new String[]{MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION}, MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION + "=?", new String[]{version}, null, null, null);
		Assert.assertEquals(1, cursor.getCount());

	}
}