package net.gility.acrida.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gility.acrida.R;
import net.gility.acrida.content.User;
import net.gility.acrida.ui.widget.AvatarView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * TweetLikeUsersAdapter.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 *
 * @data 2015-3-26 下午4:11:25
 */
public class TweetLikeUsersAdapter extends ListBaseAdapter<User> {
    
    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
	ViewHolder vh = null;
	if (convertView == null || convertView.getTag() == null) {
	    convertView = getLayoutInflater(parent.getContext()).inflate(
		    R.layout.list_cell_tweet_like_user, null);
	    vh = new ViewHolder(convertView);
	    convertView.setTag(vh);
	} else {
	    vh = (ViewHolder) convertView.getTag();
	}
	User item = mDatas.get(position);
	vh.avatar.setAvatarUrl(item.getPortrait());
	vh.name.setText(item.getName());
        return convertView;
    }
    
    static class ViewHolder {
	
	@BindView(R.id.iv_avatar)
	AvatarView avatar;
	@BindView(R.id.tv_name)
	TextView name;
	
	public ViewHolder(View view) {
	    ButterKnife.bind(this, view);
	}
    }   
}
