package com.wyt.animationclean.view;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.wyt.animationclean.AppItem;
import com.wyt.animationclean.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName GarbageClearView
 * @date: 2020/4/10 12:33
 * @author: Administrator
 * @Description: 圆环View
 */
public class GarbageClearView extends View {
	private static final String TAG = "GarbageClearView";
	private static final int UPDATE = 1 ;
	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	// 画圆环的画笔背景色
	private Paint mRingPaintBg;
	// 画字体的画笔
	private Paint mTextPaint;
	// 圆形颜色
	private int mCircleColor;
	// 圆环颜色
	private int mRingColor;
	// 圆环背景颜色
	private int mRingBgColor;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mStrokeWidth;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;
	// 字的长度
	private float mTxtWidth;
	// 字的高度
	private float mTxtHeight;
	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;
	private boolean isSingleTap=false;

	private int currentProgress= (int) getAvailMemory(getContext());
	private int max= (int) getTotalMemory(getContext());
//	private int num=0;
	private int num=(int)(((float)currentProgress/max)*100);
	//获取应用管理者
	ActivityManager am =  (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
	//获取包信息
	PackageManager pm = getContext().getPackageManager();
	public GarbageClearView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// 获取自定义的属性
		initAttrs( context, attrs );
		initVariable();
	}
	public GarbageClearView(Context context, AttributeSet attrs) {
		this(context, null, 0);
	}

	public GarbageClearView(Context context) {
		this(context, null);
	}
	//属性
	private void initAttrs( Context context, AttributeSet attrs ) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes( attrs,
				R.styleable.TasksCompletedView, 0, 0 );
		mRadius = typeArray.getDimension( R.styleable.TasksCompletedView_radius, 80 );
		mStrokeWidth = typeArray.getDimension( R.styleable.TasksCompletedView_strokeWidth, 10 );
		mCircleColor = typeArray.getColor( R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF );
		mRingColor = typeArray.getColor( R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF );
		mRingBgColor = typeArray.getColor( R.styleable.TasksCompletedView_ringBgColor, 0xFFFFFFFF );

		mRingRadius = mRadius + mStrokeWidth / 2;
	}

	@SuppressLint ("HandlerLeak")
	private Handler handler=new Handler(){
		@Override
		public void handleMessage( Message msg) {
			switch ( msg.what ){
				case UPDATE:
					setProgress( (int) msg.obj );
					Log.e( TAG, "handleMessage: "+mProgress+"====="+msg.obj  );
					mProgress=getProgress();
					break;

				default:
					break;
			}

		}
	};

	//初始化画笔
	private void initVariable() {
		//内圆
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias( true );
		mCirclePaint.setColor( mCircleColor );
		mCirclePaint.setStyle( Paint.Style.FILL );

		//外圆弧背景
		mRingPaintBg = new Paint();
		mRingPaintBg.setAntiAlias( true );
		mRingPaintBg.setColor( mRingBgColor );
		mRingPaintBg.setStyle( Paint.Style.STROKE );
		mRingPaintBg.setStrokeWidth( mStrokeWidth );


		//外圆弧
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias( true );
		mRingPaint.setColor( mRingColor );
		mRingPaint.setStyle( Paint.Style.STROKE );
		mRingPaint.setStrokeWidth( mStrokeWidth );
		//mRingPaint.setStrokeCap(Paint.Cap.ROUND);//设置线冒样式，有圆 有方

		//中间字
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias( true );
		mTextPaint.setStyle( Paint.Style.FILL );
		mTextPaint.setColor( mRingColor );
		mTextPaint.setTextSize( mRadius / 2 );

		Paint.FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil( fm.descent - fm.ascent );

		num= (int) ((float) getAvailMemory(getContext())/getTotalMemory(getContext())*100);

	}

	//开始清理
	public void startAnimation(){
		startSingleTapAnimation();
	}

	//进入界面，直接开始清理动画
	private void startSingleTapAnimation() {
		handler.postDelayed(runnbale,50);
	}
	private SingleTapRunnable runnbale=new SingleTapRunnable();
	class SingleTapRunnable implements Runnable{
		@Override
		public void run() {
//			num--;
			mProgress ++;
			if (mProgress ==0){
				invalidate();
				handler.postDelayed(runnbale,50);
			}else {
//				handler.removeCallbacks(runnbale);
//				getAllCacheSize();

				Log.e( TAG, "wenfujin: "+startCleanCache()  );
				killprocess();
//				num=(int)(((float)currentProgress/max)*100);
			}
		}
	}


	//画图
	@Override
	protected void onDraw( Canvas canvas ) {
		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;

		//内圆
		canvas.drawCircle( mXCenter, mYCenter, mRadius, mCirclePaint );

		//外圆弧背景
		RectF oval1 = new RectF();
		oval1.left = ( mXCenter - mRingRadius );
		oval1.top = ( mYCenter - mRingRadius );
		oval1.right = mRingRadius * 2 + ( mXCenter - mRingRadius );
		oval1.bottom = mRingRadius * 2 + ( mYCenter - mRingRadius );
		canvas.drawArc( oval1, 0, 360, false, mRingPaintBg ); //圆弧所在的椭圆对象、圆弧的起始角度、圆弧的角度、是否显示半径连线

		//外圆弧
		if ( mProgress > 0 ) {
			RectF oval = new RectF();
			oval.left = ( mXCenter - mRingRadius );
			oval.top = ( mYCenter - mRingRadius );
			oval.right = mRingRadius * 2 + ( mXCenter - mRingRadius );
			oval.bottom = mRingRadius * 2 + ( mYCenter - mRingRadius );
			canvas.drawArc( oval, -90, ( (float) mProgress / mTotalProgress ) * 360, false, mRingPaint ); //

			//字体
			String txt = mProgress + "%";
			mTxtWidth = mTextPaint.measureText( txt, 0, txt.length() );
			canvas.drawText( txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint );
		}
	}

	//设置进度
	public void setProgress( int progress ) {
		mProgress = progress;
		postInvalidate();//重绘
	}

	public int getProgress(){
		return mProgress;
	}


	//空余 空间
	public long getAvailMemory(Context context) {
		// 获取android当前可用内存大小
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
//		am.getMemoryInfo(mi);
		//mi.availMem; 当前系统的可用内存
		//return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
//		return mi.availMem/(1024*1024);
		return 0;
	}

	//总内存大小
	public long getTotalMemory(Context context) {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try
		{
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();
		} catch ( IOException e) {
		}
		//return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
		return initial_memory/(1024*1024);
	}

	//强制杀进程
	public void killprocess(){
		float killMemorySize = 0;   //已经清理内存
		float AllMemorySize = 0;   //总共需要清理内存

		ActivityManager activityManger=(ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<AndroidAppProcess> mProcess = AndroidProcesses.getRunningAppProcesses();
//		List<ActivityManager.RunningAppProcessInfo> list=activityManger.getRunningAppProcesses();//已经过时，只能获取自身

		for ( AndroidAppProcess amProcess :mProcess ){
			//获取缓存
			if ( amProcess.name.contains(  "com.wyt" ) || amProcess.name.startsWith( "system" ) ) {
				Log.d( TAG, "扫描不杀的进程：" + amProcess.name );
				continue;
			}
			android.os.Debug.MemoryInfo[] memoryInfo = am
					                                           .getProcessMemoryInfo(new int[] { amProcess.pid });
			int memory = memoryInfo[0].getTotalPrivateDirty();
			AllMemorySize = AllMemorySize + memory;
			Log.d( TAG, "扫描要杀的进程：" + amProcess.name );


		}


		for ( AndroidAppProcess amProcess : mProcess ) {
			if ( amProcess.name.contains(  "com.wyt" ) || amProcess.name.startsWith( "system" ) ) {
				Log.d( TAG, "跳过不杀的进程：" + amProcess.name );
				continue;
			}

			activityManger.killBackgroundProcesses(amProcess.name);
			Log.d( TAG, "杀掉的进程：" + amProcess.name );

			//获取缓存
			android.os.Debug.MemoryInfo[] memoryInfo = am
					                                           .getProcessMemoryInfo(new int[] { amProcess.pid });
			int memory = memoryInfo[0].getTotalPrivateDirty();
			killMemorySize = killMemorySize + memory;


			handler.postDelayed(runnbale,50);
			Log.e( TAG, "killprocess: "+killMemorySize +"====="+AllMemorySize  );
			Message message = new Message();
			message.obj = Math.round(killMemorySize *100/ AllMemorySize);
			message.what = UPDATE;
			handler.sendMessage(message);
//			int percent = clearSize *100/ mProcess.size();

		}
	}

	//todo 获取总缓存大小+文件缓存
	public int getAllCacheSize(){
		return (Integer.parseInt(startkillProcesses()) + Integer.parseInt( startCleanCache() ));
	}
// ################################ 清理文件 ###################################

	/**
	 * 遍历进程列表并杀死
	 * */
	public String killProcesses( ArrayList<AppItem> appInfos) {
		float killMemorySize = 0;
		for ( AppItem appInfo : appInfos) {
			if (forceStopPackageByPackageName(appInfo.getPkgName())) {
				float size = appInfo.getMemorySize();
				Log.d("1----- DYP -----1","killProcesses-->>   packageName:" + appInfo.getPkgName()+ " | size:" + size);
				killMemorySize = killMemorySize + appInfo.getMemorySize();
			}
		}
		return numToString((float) killMemorySize / 1024);
	}
	/**
	 * 使用反射方法调用系统隐藏api：forceStopPackage 通过包名杀死进程
	 * */
	public boolean forceStopPackageByPackageName(String packageName) {
		boolean forceSucceed = false;
		//获取应用管理者
		Class<ActivityManager> clazz = ActivityManager.class;
		Method method;
		try {
			method = clazz.getDeclaredMethod("forceStopPackage", String.class);
			method.invoke(am, packageName);
			forceSucceed = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return forceSucceed;
	}
	// #############################  将float转化为字符串形式返回 #########################

	public String numToString(float f) {
		// 小数不足2位,以0补足
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String s = decimalFormat.format(f);
		return s;
	}
	/**
	 * 判断是否属于系统app
	 * */
	public boolean isSystemApp( ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return false;// 表示是系统程序，但用户更新过，也算是用户安装的程序
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return false; // 一定是用户安装的程序
		}
		return true;
	}

	/**
	 * 过滤掉系统和白名单进程 获取最终要杀死的进程列表
	 * */
	public ArrayList<AppItem> getKillRunningAppProcesses() {
		ArrayList<AppItem> killRunningAppProcesses = new ArrayList<AppItem>();
		List<AppItem> runningAppProcessInfos = getRunningAppProcesses();// 正在运行的进程
		for (int i = 0; i < runningAppProcessInfos.size(); i++) {
			AppItem info = runningAppProcessInfos.get(i);
			if (!info.getSystemProcess() && !info.getFilterProcess()) {
				killRunningAppProcesses.add(info);
			}
		}
		return killRunningAppProcesses;
	}
	/**
	 * 遍历所有正在运行的进程列表,将所有应用的信息传入AppInfo中
	 * */
	public List<AppItem> getRunningAppProcesses() {
//		List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = am
//				                                                     .getRunningAppProcesses();// 正在运行的进程

		List<AndroidAppProcess> mProcess = AndroidProcesses.getRunningAppProcesses();

		List<AppItem> appInfos = new ArrayList<AppItem>();
		List<String> filterPackgeName = getFilterPackgeName();
		for (AndroidAppProcess appProcessInfo : mProcess) {
			AppItem info = new AppItem();
			int id = appProcessInfo.pid;
			info.setId(id);
			String appProcessName = appProcessInfo.name;
			info.setFilterProcess(filterPackgeName.contains(appProcessName));// 需过滤的包名
			info.setPkgName(appProcessName);// 设置进程名
			try {
				ApplicationInfo applicationInfo = pm.getPackageInfo(
						appProcessName, 0).applicationInfo;
//				Drawable icon = applicationInfo.loadIcon(pm);
//				info.setIcon(icon);
				String name = applicationInfo.loadLabel(pm).toString();
				info.setPkgName(name);
				info.setSystemProcess(isSystemApp(applicationInfo));

				android.os.Debug.MemoryInfo[] memoryInfo = am
						                                           .getProcessMemoryInfo(new int[] { id });
				int memory = memoryInfo[0].getTotalPrivateDirty();
				info.setMemorySize(memory);
				appInfos.add(info);
				info = null;
			} catch (Exception e) {
				// e.printStackTrace();
//				info.setPkgName(appProcessName);
				info.setSystemProcess(true);
			}
		}
		return appInfos;
	}
	// ################################ 清理进程 ###################################

	/**
	 * 获取进程白名单
	 * */
	private List<String> getFilterPackgeName() {

		//获取包信息
		List<String> filterPackgeNames = new ArrayList<String>(); // 过滤一些进程

		ActivityInfo launcherInfo = new Intent(Intent.ACTION_MAIN).addCategory(
				Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);

		filterPackgeNames.add(launcherInfo.packageName); // Launcher
//		filterPackgeNames.add("com.hitv.locker"); // 定时锁屏管理
		filterPackgeNames.add(getContext().getPackageName()); // 自己
//		filterPackgeNames.add(getCurTopPackgeName()); // 过滤正在运行的进程
		return filterPackgeNames;
	}

	/**
	 * 获取最顶层的app包名,若是自己，则指定为其上一个
	 * */
	public String getCurTopPackgeName() {
		String curAppTaskPackgeName = null;
		String myPackageName = getContext().getPackageName();
		List<AndroidAppProcess> mProcess = AndroidProcesses.getRunningAppProcesses();

//		List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(Integer.MAX_VALUE);
		if (mProcess.size() > 0) {
			curAppTaskPackgeName = mProcess.get(0).getPackageName();
			if (mProcess.size() > 1) {
				if (curAppTaskPackgeName.equals(myPackageName)
						    && mProcess.get(1) != null) {
					curAppTaskPackgeName = mProcess.get(1).getPackageName();
				}
			}
		}
		return curAppTaskPackgeName;
	}

	/****************************清理缓存***************************************/
	float allCacheSize = 0;
	/**
	 * 清除所有安装app 返回的总缓存
	 * */
	public String cleanCache(List<PackageInfo> packageInfos) {
		allCacheSize = 0;
		for (int i = 0; i < packageInfos.size(); i++) {
			String packageName = packageInfos.get(i).packageName;
			if (packageName != null) {
				float size = getPackageSizeInfo(packageName);
				//Log.d("2----- DYP -----2", "cleanCache-->  packageName:" + packageName+ " | size:" + size);
				if(deleteApplicationCacheFiles(packageName)){
					allCacheSize = allCacheSize + size;
					Log.d("2----- DYP -----2", "cleanCache-->  packageName:" + packageName+ " | size:" + size);
				}
			}
		}
		return numToString((float) allCacheSize / 1024);
	}

	float cacheSize = 0;

	/**
	 * 使用反射方法调用系统隐藏api： getPackageSizeInfo 获取app缓存大小
	 * */
	public float getPackageSizeInfo(final String packageName) {
		cacheSize = 0;
		try {
			Method method = PackageManager.class.getMethod(
					"getPackageSizeInfo", String.class,
					IPackageStatsObserver.class );
			method.invoke(pm, packageName,
					new IPackageStatsObserver.Stub() {
						@Override
						public void onGetStatsCompleted( PackageStats pStats,
						                                 boolean succeeded) throws RemoteException {
							cacheSize = pStats.cacheSize;
						}
					} );
		} catch (Exception e) {
			cacheSize = 0;
			// e.printStackTrace();
		}
		return cacheSize;
	}


	boolean isCleanCacheSucceed = false;
	/**
	 * 使用反射方法调用系统隐藏api： deleteApplicationCacheFiles 返回清除是否成功
	 * */
	private boolean deleteApplicationCacheFiles(String packageName) {
		isCleanCacheSucceed = false;

		try {
			Method method = PackageManager.class.getMethod(
					"deleteApplicationCacheFiles", new Class[] { String.class,
							IPackageDataObserver.class });
			method.invoke(pm, packageName, new IPackageDataObserver.Stub() {
				@Override
				public void onRemoveCompleted(String packageName,
				                              boolean succeeded) throws RemoteException {
					if(succeeded){
						Log.d("DYP", "deleteApplicationCacheFiles -->> succeeded "+succeeded);
						Log.d("DYP", "deleteApplicationCacheFiles -->> packageName "+packageName);
					}
					isCleanCacheSucceed = succeeded;
				}
			});
		} catch (Exception e) {
			// e.printStackTrace();
			Log.d("DYP", "deleteApplicationCacheFiles -->> catch ");
		}
		return isCleanCacheSucceed;
	}
	/**
	 * 获取所有已经安装的应用程序 ,包括那些卸载了的，但没有清除数据的应用程序
	 * */
	public List<PackageInfo> getPackageInfos() {

		List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		// List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		return packageInfos;
	}


	//获取 清理进程缓存的大小
	public String startkillProcesses() {
		return killProcesses(getKillRunningAppProcesses());
	}
	//获取文件清理 垃圾的大小
	public String startCleanCache() {
		return cleanCache(getPackageInfos());
	}
}