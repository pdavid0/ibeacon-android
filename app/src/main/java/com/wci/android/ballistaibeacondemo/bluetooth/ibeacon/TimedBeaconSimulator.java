package com.wci.android.ballistaibeacondemo.bluetooth.ibeacon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matt Tyler on 4/18/14.
 */
public class TimedBeaconSimulator implements com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.simulator.BeaconSimulator {
    protected static final String TAG = "TimedBeaconSimulator";
    private List<IBeacon> iBeacons;

    /*
     * You may simulate detection of iBeacons by creating a class like this in your project.
     * This is especially useful for when you are testing in an Emulator or on a device without BluetoothLE capability.
     *
     * Uncomment lines 36, 37, and 139 - 142 of MonitoringActivity.java and
     * set USE_SIMULATED_IBEACONS = true to initialize the sample code in this class.
     * If using a bluetooth_old incapable test device (i.e. Emulator), you will want to comment
     * out the verifyBluetooth() call on line 32 of MonitoringActivity.java as well.
     *
     * Any simulated iBeacons will automatically be ignored when building for production.
     */
    public boolean USE_SIMULATED_IBEACONS = false;

    /**
     * Creates empty iBeacons ArrayList.
     */
    public TimedBeaconSimulator() {
        iBeacons = new ArrayList<IBeacon>();
    }

    /**
     * Required getter method that is called regularly by the Android iBeacon Library.
     * Any iBeacons returned by this method will appear within your test environment immediately.
     */
    public List<IBeacon> getBeacons() {
        return iBeacons;
    }

    /**
     * Creates simulated iBeacons all at once.
     */
    public void createBasicSimulatedBeacons() {
        if (USE_SIMULATED_IBEACONS) {
            IBeacon iBeacon1 = new IBeacon("DF7E1C79-43E9-44FF-886F-1D1F7DA6997A".toLowerCase(),
                    1, 1);
            IBeacon iBeacon2 = new IBeacon("DF7E1C79-43E9-44FF-886F-1D1F7DA6997B".toLowerCase(),
                    1, 2);
            IBeacon iBeacon3 = new IBeacon("DF7E1C79-43E9-44FF-886F-1D1F7DA6997C".toLowerCase(),
                    1, 3);
            IBeacon iBeacon4 = new IBeacon("DF7E1C79-43E9-44FF-886F-1D1F7DA6997D".toLowerCase(),
                    1, 4);
            iBeacons.add(iBeacon1);
            iBeacons.add(iBeacon2);
            iBeacons.add(iBeacon3);
            iBeacons.add(iBeacon4);


        }
    }


    private ScheduledExecutorService scheduleTaskExecutor;


    /**
     * Simulates a new iBeacon every 10 seconds until it runs out of new ones to add.
     */
    public void createTimedSimulatedBeacons() {
        if (USE_SIMULATED_IBEACONS) {
            iBeacons = new ArrayList<IBeacon>();
            IBeacon iBeacon1 = new IBeacon("DF7E1C79-43E9-44FF-886F-1D1F7DA6997A".toLowerCase(), 1, 1);
            IBeacon iBeacon2 = new IBeacon("DF7E1C79-43E9-44FF-886F-1D1F7DA6997B".toLowerCase(), 1, 2);
            IBeacon iBeacon3 = new IBeacon("DF7E1C79-43E9-44FF-886F-1D1F7DA6997C".toLowerCase(), 1, 3);
            IBeacon iBeacon4 = new IBeacon("DF7E1C79-43E9-44FF-886F-1D1F7DA6997D".toLowerCase(), 1, 4);
            iBeacons.add(iBeacon1);
            iBeacons.add(iBeacon2);
            iBeacons.add(iBeacon3);
            iBeacons.add(iBeacon4);

            final List<IBeacon> finalIBeacons = new ArrayList<IBeacon>(iBeacons);

            //Clearing iBeacons list to prevent all iBeacons from appearing immediately.
            //These will be added back into the iBeacons list from finalIBeacons later.
            iBeacons.clear();

            scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

            // This schedules an iBeacon to appear every 10 seconds:
            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    try {
                        //putting a single iBeacon back into the iBeacons list.
                        if (finalIBeacons.size() > iBeacons.size())
                            iBeacons.add(finalIBeacons.get(iBeacons.size()));
                        else
                            scheduleTaskExecutor.shutdown();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 10, TimeUnit.SECONDS);
        }
    }

}