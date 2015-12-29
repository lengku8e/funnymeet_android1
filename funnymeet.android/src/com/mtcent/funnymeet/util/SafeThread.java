package com.mtcent.funnymeet.util;


//interface SafeThreadInterfacse {
//	public void onStart();
//	public boolean runUntilFalse();
//	public void onFinsh();
//}
////implements SafeThreadInterfacse 
public abstract class SafeThread {

	Object Lock= new Object();
	boolean run = true;
	boolean bcancle = false;
	long intervalTime = 0;
	Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {
			onStart();
			while (run && bcancle == false && runUntilFalse()) {
				if(intervalTime>0){
					sleep(intervalTime);
				}
			};
			
			onFinsh();
			run = false;
			synchronized (Lock) {
				Lock.notifyAll();
			}
		}
	});

	public void onStart() {

	}
	public SafeThread(){
		
	}
	public SafeThread(long intervalTime){
		SafeThread.this.intervalTime=intervalTime;
	}
	// ����ʵ��Ľӿ�����
	public abstract boolean runUntilFalse();

	public void onFinsh() {

	}

	// ��ȫֹͣ,δ��ʱ����true����ʱ����false
	public boolean stopWaitFor(long maxTime) {
		try {

			if (isStop()==false) {
				synchronized (Lock) {
					cancle();
					thread.interrupt();
					Lock.wait(maxTime);
				}
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	public synchronized SafeThread start() {
		run = true;
		bcancle = false;
		thread.start();
		return this;
	}

	public boolean isStop() {
		return (run==false);
	}

	public void cancle() {
		bcancle = true;
	}
	public boolean  isCancle() {
		return bcancle == true;
	}
	public static void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}