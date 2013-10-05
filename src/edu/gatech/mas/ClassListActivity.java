package edu.gatech.mas;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.gatech.mas.service.GPSLocationService;

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
	private ClassListPagerAdapter mClassListPagerAdapter;
	/**
	 * View pager for tabs.
	 */
	private ViewPager mViewPager;

	private Button mLocationButton;
	

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_list);

		mClassListPagerAdapter = new ClassListPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mClassListPagerAdapter);

		mLocationButton = (Button) findViewById(R.id.enableLocationButton);
		mLocationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getBaseContext().startService(
						new Intent(ClassListActivity.this,
								GPSLocationService.class));
			}
		});
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
		return 6;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO: add class name from t-square
		return "Class " + (position + 1);
	}

}
