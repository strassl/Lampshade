package eu.prismsw.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/** Shows a Dialog with a SeekBar and saves the result.
 * Minimum, maximum, default value and units can be customised via xml
 */
public class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener {
	public static String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
	public static String LAMPSHADE_NAMESPACE = "http://schemas.android.com/apk/res/eu.prismsw.lampshade";
	
	public static String ATTR_SUMMARY = "summary";
	public static String ATTR_MIN_VALUE = "minValue";
	public static String ATTR_DEFAULT_VALUE = "defaultValue";
	public static String ATTR_MAX_VALUE = "maxValue";
	public static String ATTR_UNITS = "units";
	
	private Context context;
	private SeekBar seekBar;
	private TextView tvValue;
	
	private String summary;
	private Integer minValue;
	private Integer defaultValue;
	private Integer maxValue;
	private String units;
	
	private Integer currentValue;
	

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		
		this.summary = attrs.getAttributeValue(ANDROID_NAMESPACE, ATTR_SUMMARY);
		
		this.minValue = attrs.getAttributeIntValue(LAMPSHADE_NAMESPACE, ATTR_MIN_VALUE, 0);
		this.defaultValue = attrs.getAttributeIntValue(ANDROID_NAMESPACE, ATTR_DEFAULT_VALUE, 50);
		this.currentValue = this.defaultValue;
		this.maxValue = attrs.getAttributeIntValue(LAMPSHADE_NAMESPACE, ATTR_MAX_VALUE, 100);
		this.units = attrs.getAttributeValue(LAMPSHADE_NAMESPACE, ATTR_UNITS);
	}
	
	@Override
	protected View onCreateDialogView() {
		LinearLayout layout = new LinearLayout(this.context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 15, 6, 8);
		
		this.tvValue = new TextView(context);
		tvValue.setText(getValueString());
		tvValue.setGravity(Gravity.CENTER);
		layout.addView(tvValue);
		
		this.seekBar = new SeekBar(context);
		seekBar.setOnSeekBarChangeListener(this);
		seekBar.setProgress(valueToSeekBarProgress(this.currentValue));
		seekBar.setMax(valueToSeekBarProgress(this.maxValue));
		layout.addView(seekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		return layout;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		
		if(which ==DialogInterface.BUTTON_POSITIVE) {
			if(callChangeListener(this.currentValue)) {
				persistInt(this.currentValue);
			}
		}
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		super.onSetInitialValue(restorePersistedValue, defaultValue);
		
		if (restorePersistedValue) {
			this.currentValue = this.getPersistedInt(this.defaultValue);
		}
		else {
			this.currentValue = (Integer)defaultValue;
		}
	}
	
	private Integer seekBarProgressToValue(Integer progress) {
		return progress + minValue;
	}
	
	private Integer valueToSeekBarProgress(Integer value) {
		return value - minValue;
	}
	
	private String getValueString() {
		String str = this.currentValue.toString();
		
		if(this.units != null) {
			str += this.units;
		}
		
		return str;
	}
	
	public void setProgress(Integer progress) {
		seekBar.setProgress(progress);
		setValue(seekBarProgressToValue(progress));
	}
	
	public void setValue(Integer value) {
		this.currentValue = value;
		this.tvValue.setText(getValueString());
		
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		setValue(seekBarProgressToValue(progress));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
