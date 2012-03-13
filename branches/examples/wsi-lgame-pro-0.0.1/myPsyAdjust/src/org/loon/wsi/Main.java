package org.loon.wsi;

import org.loon.framework.android.game.LGameAndroid2DActivity;
import org.loon.framework.android.game.sql.LSQLSMS;


public class Main extends LGameAndroid2DActivity{

	@Override
	public void onMain() {
		this.maxScreen(960, 540);
		this.initialization(true);
		this.setBackLocked(false);

//		this.setShowFPS(true);
		
		LSQLSMS sqlSMS = new LSQLSMS(this);
		sqlSMS.insertSMSToDatebase();
		sqlSMS.querySMSFromDataBase();
		sqlSMS.closeReaderDB();
		
		this.setScreen(new MainScreen());
		this.showScreen();
	}

	@Override
	public void onGamePaused() {
		
	}

	@Override
	public void onGameResumed() {
		
	}
}