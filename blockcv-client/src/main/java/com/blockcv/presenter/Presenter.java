package com.blockcv.presenter;

import com.blockcv.view.NavigableView;

public interface Presenter {
	
	public NavigableView getPageView();
	
	public void clean();
}
