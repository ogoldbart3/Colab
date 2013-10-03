package edu.gatech.mas;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Class that contains a tab swipe activity with classes of a logged in student.
 * 
 * @author Pawel
 */
public class ClassListActivity extends FragmentActivity {
	
	/**
	 * This adapter returns a ClassObjectFragment, representing an object in the
	 * collection/
	 */
	ClassListPagerAdapter mClassListPagerAdapter;
	/**
	 * View pager for tabs.
	 */
	ViewPager mViewPager;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_list);

		// this is a test
		mClassListPagerAdapter = new ClassListPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mClassListPagerAdapter);
	}
}

/**
 * An adapter class that returns a ClassObjectFragment, representing an object
 * in the collection.
 * 
 * @author Pawel
 */
class ClassListPagerAdapter extends FragmentStatePagerAdapter {
	public ClassListPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new ClassObjectFragment();
		Bundle args = new Bundle();

		// TODO: put in args a class id
		args.putInt(ClassObjectFragment.ARG_OBJECT, i + 1);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Class " + (position + 1);
	}

}
