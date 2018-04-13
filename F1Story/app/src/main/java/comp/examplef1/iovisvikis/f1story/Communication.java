package comp.examplef1.iovisvikis.f1story;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import comp.examplef1.iovisvikis.f1story.AsyncTasks.DownloadFragment;

public interface Communication
{
    boolean isResultDrawer();
    boolean hasInternetConnection();
    boolean apiResponds();
    boolean isSoundsOn();
    boolean getFromPreferences(String key, boolean defaultValue);

    DownloadFragment getDownloadFragment();
    SQLiteDatabase getAppDatabase();

    void setResultFragment(RecyclerView.Adapter adapterToSet);
    void cancelAllNotifications();
    void setNotificationsOn(boolean on);
    void onDialogPositiveClick(String userInput, String key);
    void launchSingleSelectionDialog(Bundle args);
    void launchMultipleSelectionDialog(Bundle args);
    void writeToPreferences(String key, boolean value);
    void blockOrientationChanges();
    void allowOrientationChanges();
    void setPlayStartupSound(boolean onOrOff);
}
