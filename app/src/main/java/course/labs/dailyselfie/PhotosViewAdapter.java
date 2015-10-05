package course.labs.dailyselfie;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PhotosViewAdapter extends BaseAdapter {

	private ArrayList<Selfie> list = new ArrayList<Selfie>();
	private static LayoutInflater inflater = null;
	private Context mContext;

	public PhotosViewAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;

        Selfie curr = list.get(position);

        if (null == convertView) {
            holder = new ViewHolder();
            newView = inflater.inflate(R.layout.list_item, parent, false);
            holder.thumbnail = (ImageView) newView.findViewById(R.id.item_thumbnail);
            holder.imageName = (TextView) newView.findViewById(R.id.item_text);
            newView.setTag(holder);

        } else {
            holder = (ViewHolder) newView.getTag();
        }

        holder.thumbnail.setImageBitmap(curr.getThumbnail());
        holder.imageName.setText(curr.getName());
        return newView;
	}

	static class ViewHolder {

		ImageView thumbnail;
		TextView imageName;
	}


	public void add(Selfie listItem) {
		list.add(listItem);
		notifyDataSetChanged();
	}

	public ArrayList<Selfie> getList() {
		return list;
	}

    public void setList(ArrayList<Selfie> list) {this.list = list; }

	public void removeAllViews() {
		list.clear();
		this.notifyDataSetChanged();
	}
}
