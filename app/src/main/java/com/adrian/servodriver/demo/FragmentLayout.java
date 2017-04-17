package com.adrian.servodriver.demo;

/*============================================================================
* 
* (C) Copyright 2014, Future Technology Devices International Ltd.
* ============================================================================
*
* This source code ("the Software") is provided by Future Technology Devices
* International Limited ("FTDI") subject to the licence terms set out
* http://www.ftdichip.com/FTSourceCodeLicenceTerms.htm ("the Licence Terms").
* You must read the Licence Terms before downloading or using the Software.
* By installing or using the Software you agree to the Licence Terms. If you
* do not agree to the Licence Terms then do not download or use the Software.
*
* Without prejudice to the Licence Terms, here is a summary of some of the key
* terms of the Licence Terms (and in the event of any conflict between this
* summary and the Licence Terms then the text of the Licence Terms will
* prevail).
*
* The Software is provided "as is".
* There are no warranties (or similar) in relation to the quality of the
* Software. You use it at your own risk.
* The Software should not be used in, or for, any medical device, system or
* appliance. There are exclusions of FTDI liability for certain types of loss
* such as: special loss or damage; incidental loss or damage; indirect or
* consequential loss or damage; loss of income; loss of business; loss of
* profits; loss of revenue; loss of contracts; business interruption; loss of
* the use of money or anticipated savings; loss of information; loss of
* opportunity; loss of goodwill or reputation; and/or loss of, damage to or
* corruption of data.
* There is a monetary cap on FTDI's liability.
* The Software may have subsequently been amended by another user and then
* distributed by that other user ("Adapted Software").  If so that user may
* have additional licence terms that apply to those amendments. However, FTDI
* has no liability in relation to those amendments.
* ============================================================================*/


import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.ftdi.j2xx.D2xxManager;



public class FragmentLayout extends Activity {
	public static D2xxManager ftD2xx = null;
	public static int currect_index = 0;
	public static int old_index = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
    		ftD2xx = D2xxManager.getInstance(this);
    	} catch (D2xxManager.D2xxException ex) {
    		ex.printStackTrace();
    	}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);
        
		IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
    }

    public static class DetailsActivity extends Activity {

    	Map<Integer, Fragment> act_map = new HashMap<Integer, Fragment>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                finish();
                return;
            }

            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.
                Fragment f = act_map.get(currect_index);
                if (f == null)
                {
                	switch (currect_index)
                	{
                		case 0:
                			f = new DeviceInformationFragment(this , ftD2xx);
                			break;
                		case 1:
                			f = new FT4222SPIFragment(this , ftD2xx);
                			break;
                		case 2:
                			f = new FT4222I2CFragment(this , ftD2xx);
                			break;
                		case 3:
                			f = new FT4222GPIOFragment(this , ftD2xx);
                			break;               			

                		default:
                			f = new DetailsFragment();
                			break;
                	}

                	act_map.put(currect_index, f);
                	f.setArguments(getIntent().getExtras());
                	getFragmentManager().beginTransaction().add(android.R.id.content, f).commit();
                }
            }
        }
    }
    /**
     * This is the "top-level" fragment, showing a list of items that the
     * user can pick.  Upon picking an item, it takes care of displaying the
     * data to the user as appropriate based on the currrent UI layout.
     */

    public static class TitlesFragment extends ListFragment {
        boolean mDualPane;
        int mCurCheckPosition = 0;
        Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();

        public TitlesFragment() {

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Populate list with our static array of titles.
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_activated_1, FtdiModeListInfo.TITLES));

            // Check to see if we have a frame in which to embed the details
            // fragment directly in the containing UI.
            View detailsFrame = getActivity().findViewById(R.id.details);

            mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }

            if (mDualPane) {
                // In dual-pane mode, the list view highlights the selected item.
                getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
                showDetails(mCurCheckPosition);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            showDetails(position);
        }

        /**
         * Helper function to show the details of a selected item, either by
         * displaying a fragment in-place in the current UI, or starting a
         * whole new activity in which it is displayed.
         */
        void showDetails(int index) {
            mCurCheckPosition = index;
            currect_index = index;
            if (mDualPane) {
            	// We can display everything in-place with fragments, so update
            	// the list to highlight the selected item and show the data.
                getListView().setItemChecked(index, true);
                Fragment f = map.get(index);
                if (f == null) {
                	switch (index) {
                	case 0:
                		f = new DeviceInformationFragment(getActivity() , ftD2xx);
                		break;
                	case 1:
            			f = new FT4222SPIFragment(getActivity() , ftD2xx);
            			break;
                	case 2:
            			f = new FT4222I2CFragment(getActivity() , ftD2xx);
            			break;
               		case 3:
                		f = new FT4222GPIOFragment(getActivity() , ftD2xx);
                		break;
             			
                	default:
                		f = new DetailsFragment();
                		break;
                	}
                	
                	map.put(index, f);
                	Bundle args = new Bundle();
                	args.putInt("index", index);
                	f.setArguments(args);
                }
                
                if ( currect_index != old_index ) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, f);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                }
                old_index = currect_index;
            }
            else
            {
                // Otherwise we need to launch a new activity to display
                // the dialog fragment with selected text.
                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailsActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        }
    }

    /**
     * This is the secondary fragment, displaying the details of a particular
     * item.
     */
    public static class DetailsFragment extends Fragment {
    	public DetailsFragment() {

    	}

        public int getShownIndex() {
            return getArguments().getInt("index", -1);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            if (container == null) {
                // We have different layouts, and in one of them this
                // fragment's containing frame doesn't exist.  The fragment
                // may still be created from its saved state, but there is
                // no reason to try to create its view hierarchy because it
                // won't be displayed.  Note this is not needed -- we could
                // just run the code below, where we would create and return
                // the view hierarchy; it would just never be used.
                return null;
            }

            ScrollView scroller = new ScrollView(getActivity());
            TextView text = new TextView(getActivity());
            int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    4, getActivity().getResources().getDisplayMetrics());
            text.setPadding(padding, padding, padding, padding);
            scroller.addView(text);
            text.setText(FtdiModeListInfo.DIALOGUE[getShownIndex()]);
            return scroller;
        }
    }
}
