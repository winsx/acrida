package net.gility.acrida.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gility.acrida.R;
import net.gility.acrida.content.Favorite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserFavoriteAdapter extends ListBaseAdapter<Favorite> {
	
	static class ViewHolder {
		
		@BindView(R.id.tv_favorite_title) TextView title;
		
		public ViewHolder(View view) {
			ButterKnife.bind(this,view);
		}
	}

	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_favorite, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		Favorite favorite = (Favorite) mDatas.get(position);
		
		vh.title.setText(favorite.getTitle());
		return convertView;
	}

}
