package com.wyt.animationclean;

import android.graphics.drawable.Drawable;

public class AppItem {
	private String pkgName= null;
	private String appName=null;
	private Boolean IsFilterProcess;
	private Boolean IsSystemProcess;
	private int id;
	private int MemorySize;
	private Drawable icon = null;
	private boolean isSelected = false;
	public String getPkgName() {
		return pkgName;
	}
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public int getMemorySize() {
		return MemorySize;
	}

	public void setMemorySize( int memorySize ) {
		MemorySize = memorySize;
	}

	public Boolean getFilterProcess() {
		return IsFilterProcess;
	}

	public void setFilterProcess( Boolean filterProcess ) {
		IsFilterProcess = filterProcess;
	}

	public Boolean getSystemProcess() {
		return IsSystemProcess;
	}

	public void setSystemProcess( Boolean systemProcess ) {
		IsSystemProcess = systemProcess;
	}
}
