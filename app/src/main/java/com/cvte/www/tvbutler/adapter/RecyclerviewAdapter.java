package com.cvte.www.tvbutler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvte.www.tvbutler.R;
import com.cvte.www.tvbutler.entity.AppInfo;
import com.cvte.www.tvbutler.utils.Utils;

import java.util.List;


/**
 * Created by WuWeiLong on 2017/10/17.
 * Function：给使用的 recyclerview 准备的的适配器
 */

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context context;
    private List<AppInfo> list;

    public void refresh(List<AppInfo> list) {
        this.list = list;
    }

    public List<AppInfo> getList(){
        return list;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public interface OnCheckedChangeListener{
        void onCheckedChanged(View view,boolean isChecked,String packageName);
    }



    private OnItemClickListener onItemClickListener;
    private OnCheckedChangeListener onCheckedChangeListener;



    public RecyclerviewAdapter(Context context, List<AppInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener){
        this.onCheckedChangeListener = onCheckedChangeListener;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 : list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent,
                    false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_footer, parent,
                    false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            AppInfo appInfo = list.get(position);
            ((ItemViewHolder) holder).name.setText(appInfo.getName());
            ((ItemViewHolder) holder).packageName.setText(appInfo.getPackageName());
            ((ItemViewHolder) holder).pid.setText(appInfo.getPid()+"");

            //列表中的后台应用，从持久化白名单中检查是否存在
            ((ItemViewHolder) holder).checkBox.setChecked(Utils.getWhiteListBean().getmWhiteList()
                    .contains(((ItemViewHolder) holder).packageName.getText().toString()));


            if (appInfo.getIcon() != null) {
                //((ItemViewHolder) holder).icon.setVisibility(View.VISIBLE);

                ((ItemViewHolder) holder).icon.setImageDrawable(appInfo.getIcon());
            } else {
                //((ItemViewHolder) holder).icon.setVisibility(View.GONE);
            }
            if (onCheckedChangeListener != null){
                ((ItemViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        onCheckedChangeListener.onCheckedChanged(((ItemViewHolder) holder)
                                .checkBox,isChecked,((ItemViewHolder) holder).packageName.getText().toString());
                    }
                });
            }
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        if (((ItemViewHolder) holder).checkBox.isChecked()){
                            ((ItemViewHolder) holder).checkBox.setChecked(false);
                        }else{
                            ((ItemViewHolder) holder).checkBox.setChecked(true);
                        }

                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();

                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
            }
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, packageName, pid; // 应用名、应用包名、进程id
        ImageView icon; // 图片
        CheckBox checkBox;
        public ItemViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            packageName = (TextView) view.findViewById(R.id.packageName);
            pid = (TextView) view.findViewById(R.id.pid);
            icon = (ImageView) view.findViewById(R.id.icon);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {
        public FootViewHolder(View view) {
            super(view);
        }
    }

}
