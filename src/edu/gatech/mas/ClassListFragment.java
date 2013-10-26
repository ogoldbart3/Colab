package edu.gatech.mas;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import edu.gatech.mas.model.Course;
import edu.gatech.mas.model.Student;

/**
 * A fragment class representing a class that student participates in.
 * 
 * @author Pawel
 */
public class ClassListFragment extends Fragment {

	public static final String ARG_OBJECT = "object";
	public static String testString = "TestString";
	TableLayout tableLayout;

	private Course mCourse;

	/**
	 * Helper class that converts dp to px.
	 * 
	 * @param dpValue
	 *            value in dp
	 * @return value in px
	 */
	private int convertDpToPx(float dpValue) {
		Resources r = getActivity().getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpValue, r.getDisplayMetrics());
		return px;
	}

	static ClassListFragment newInstance(Course course) {
		ClassListFragment c = new ClassListFragment();

		Bundle args = new Bundle();
		args.putParcelable("course", course);
		c.setArguments(args);

		return c;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null)
			mCourse = getArguments().getParcelable("course");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_fragment, container,
				false);

		if (mCourse != null)
			System.out.println("Now: " + mCourse.getName() + " and students "
					+ mCourse.getStudents().size());
		else
			System.out.println("Saved instance is null (mCourse)");

		tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout1);

		for (int i = 0; i < mCourse.getStudents().size(); i++) {

			final Student currentStudent = mCourse.getStudents().get(i);

			System.out.println(i + ": " + currentStudent.toString());

			// Create row
			final TableRow tableRow = new TableRow(getActivity());
			TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
					TableLayout.LayoutParams.WRAP_CONTENT,
					TableLayout.LayoutParams.WRAP_CONTENT);

			int leftMargin = convertDpToPx(10);
			int topMargin = convertDpToPx(0);
			int rightMargin = convertDpToPx(5);
			int bottomMargin = convertDpToPx(5);

			tableRowParams.setMargins(leftMargin, topMargin, rightMargin,
					bottomMargin);
			tableRow.setLayoutParams(tableRowParams);

			TableRow.LayoutParams tableParams = new TableRow.LayoutParams(
					TableLayout.LayoutParams.WRAP_CONTENT,
					TableLayout.LayoutParams.WRAP_CONTENT, 1);

			TableRow.LayoutParams tableParams2 = new TableRow.LayoutParams(
					TableLayout.LayoutParams.WRAP_CONTENT,
					TableLayout.LayoutParams.WRAP_CONTENT, 4);

			// Creation textView
			final TextView student = new TextView(getActivity());
			student.setText(currentStudent.getUsername());
			student.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			student.setLayoutParams(tableParams2);

			final TextView status = new TextView(getActivity());
			status.setText("Status");
			status.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			status.setLayoutParams(tableParams);

			final TextView distance = new TextView(getActivity());
			distance.setText("Distance");
			distance.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			distance.setLayoutParams(tableParams);

			final TextView map = new TextView(getActivity());
			map.setText("Map");
			map.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			map.setLayoutParams(tableParams);

			tableRow.addView(student);
			tableRow.addView(status);
			tableRow.addView(distance);
			tableRow.addView(map);

			tableRow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(),
							StudentInfoActivity.class);
					i.putExtra(StudentInfoActivity.USERNAME_TAG,
							currentStudent.getUsername());
					i.putExtra("student", currentStudent);
					startActivity(i);
				}
			});

			tableLayout.addView(tableRow);
		}
		return rootView;
	}

	public void displayRow() {

		if(tableLayout != null) {
			System.out.println("table lauout is not null");
			
		}
		else System.out.println("table layout is null");
		
		if(mCourse !=null)
			System.out.println(mCourse.getName());
		else
			System.out.println("mCourse is null");
	}

	
}
