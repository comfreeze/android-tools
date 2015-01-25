package net.comfreeze.lib.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract public class CFZService extends Service {
    private static final String TAG = CFZService.class.getSimpleName();
    public static boolean silent = false;
    public boolean persistent = false;
    protected ConcurrentLinkedQueue<CFZWorkerItem> workQueue;
    protected CFZWorkerThread workerThread;
    ArrayList<Messenger> clients = new ArrayList<Messenger>();

    abstract protected int getNotificationId();

    abstract protected void handleIntent(Intent intent);

    abstract protected String getThreadTag();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG, "Received start ID: " + startId + ": " + intent);
        //handleIntent(intent);
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        if (persistent) {
            // Run until explicitly stopped
            return START_STICKY;
        } else {
            // Default - die when no more clients
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeWorkQueue();
        Log.w(TAG, "Well, hello there... you come here often?");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "I'M MELtInG... mel..tin...g...");
    }

    protected void initializeWorkQueue() {
        workQueue = new ConcurrentLinkedQueue<CFZWorkerItem>();
        workerThread = new CFZWorkerThread(workQueue, getThreadTag());
        workerThread.start();
    }

    protected void addRunnable(CFZWorkerItem work) {
        // cancelPreviousWorkItem(work);
        synchronized (workQueue) {
            workQueue.add(work);
            workQueue.notifyAll();
        }
    }

    protected void scheduleRunnable(CFZWorkerItem work) {
        // cancelPreviousWorkItem(work);
        synchronized (workQueue) {
            if (!workQueue.contains(work) && !work.equals(workerThread.getActive())) {
                workQueue.add(work);
                workQueue.notifyAll();
            }
        }
    }

    protected void schedulePriorityRunnable(CFZWorkerItem work) {
        synchronized (workQueue) {
            cancelActiveWorkItem(work);
            workQueue.add(work);
            workQueue.notifyAll();
        }
    }

    protected void replaceRunnable(CFZWorkerItem work) {
        synchronized (workQueue) {
            cancelPreviousWorkItem(work);
            workQueue.add(work);
            workQueue.notifyAll();
        }
    }

    protected void cancelPreviousWorkItem(CFZWorkerItem work) {
        synchronized (workQueue) {
            if (workQueue.contains(work))
                workQueue.remove(work);
            workQueue.notifyAll();
        }
    }

    protected void cancelActiveWorkItem(CFZWorkerItem work) {
        cancelPreviousWorkItem(work);
        if (work.equals(workerThread.active))
            workerThread.interrupt();
    }

    protected void showNotification(CharSequence message) {
        Log.d(TAG, "Showing notification");
    }

    protected void registerClient(Message message) {
        Log.d(TAG, "Registering client");
        if (null != message.replyTo)
            clients.add(message.replyTo);
        // sendMessage(message.replyTo, Message.obtain(null,
        // ResponseType.REGISTERED.ordinal()));
    }

    protected void unregisterClient(Message message) {
        Log.d(TAG, "Unregistering client");
        if (null != message.replyTo)
            clients.remove(message.replyTo);
    }

    protected void sendMessage(Messenger client, Message msg) {
        Log.d(TAG, "Sending mesage");
        try {
            client.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "Exception", e);
            clients.remove(client);
        }
    }

    protected void sendMessage(Message msg) {
        for (int i = clients.size() - 1; i >= 0; i--) {
            sendMessage(clients.get(i), msg);
        }
    }

    protected void sendMessage(int code) {
        sendMessage(Message.obtain(null, code));
    }

    public static final class RESULT {
        public static final int OK = Activity.RESULT_OK;
        public static final int CANCELED = Activity.RESULT_CANCELED;
        public static final int FIRST_USER = Activity.RESULT_FIRST_USER;
        public static final int ERROR = 400;
    }

    public static class CFZWorkerThread extends Thread {
        private final ConcurrentLinkedQueue<CFZWorkerItem> workQueue;
        public CFZWorkerItem active;

        public CFZWorkerThread(ConcurrentLinkedQueue<CFZWorkerItem> workQueue, String name) {
            super(name);
            this.workQueue = workQueue;
        }

        private CFZWorkerItem getWork() {
            synchronized (workQueue) {
                try {
                    while (workQueue.isEmpty())
                        workQueue.wait();
                    return workQueue.remove();
                } catch (InterruptedException ie) {
                    throw new AssertionError(ie);
                }
            }
        }

        public Runnable getActive() {
            return this.active;
        }

        public void run() {
            for (; ; ) {
                active = getWork();
                active.run();
            }
        }
    }

    abstract public static class CFZWorkerItem implements Runnable {
        private final Intent intent;
        private final int what;

        public CFZWorkerItem(Intent intent, int what) {
            this.intent = intent;
            this.what = what;
        }

        public void run() {
            processItem(intent, what);
        }

        public boolean equals(CFZWorkerItem item) {
            if (null != item && null != item.intent
        /* */ && (null != item.intent.getAction()
	    /* */ && null != intent
	    /* */ && item.intent.getAction().equals(intent.getAction()))
	    /* */ && item.what == what)
                return true;
            return false;
        }

        abstract public void processItem(Intent intent, int what);
    }
}
