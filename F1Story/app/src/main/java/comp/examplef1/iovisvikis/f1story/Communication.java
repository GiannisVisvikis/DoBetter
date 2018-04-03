package comp.examplef1.iovisvikis.f1story;


import android.support.v7.widget.RecyclerView;

interface Communication
{

    boolean isResultDrawer();

    void setResultFragment(RecyclerView.Adapter adapterToSet);
}
