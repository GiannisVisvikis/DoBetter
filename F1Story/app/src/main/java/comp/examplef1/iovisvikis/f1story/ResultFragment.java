package comp.examplef1.iovisvikis.f1story;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ResultFragment extends Fragment
{


    private RecyclerView.Adapter currentAdapter;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (recyclerView != null)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        if (currentAdapter != null)
        {
            //don't play music
            recyclerView.setBackgroundColor(getActivity().getResources().getColor(R.color.recycler_background));
            recyclerView.setAdapter(currentAdapter);
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.result_fragment_layout, container, false);

        recyclerView = rootView.findViewById(R.id.result_fragment_recycler_view);

        return rootView;
    }



    public void setTheAdapter(RecyclerView.Adapter adapter)
    {
        currentAdapter = adapter;

        if(recyclerView.getLayoutManager() == null)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        recyclerView.setBackgroundColor(getActivity().getResources().getColor(R.color.recycler_background));
        recyclerView.setAdapter(currentAdapter);
    }


}


