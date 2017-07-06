package com.aiminerva.oldpeople.service;

import com.creative.base.Ireader;

import java.io.IOException;

public class ReaderBLE implements Ireader{
	
	private BLEHelper mHelper;

	public ReaderBLE(BLEHelper helper) {
		mHelper = helper;
	}
	
	@Override
	public int read(byte[] buffer) throws IOException {
		return mHelper.read(buffer);
	}

	@Override
	public void close() {
		mHelper = null;
	}

	@Override
	public void clean() {
		mHelper.clean();
	}

	@Override
	public int available() throws IOException {
		if(mHelper!=null){
			return mHelper.available();
		}
		return 0;
	}

}
