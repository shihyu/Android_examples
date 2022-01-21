package com.krunal.choosedirectory.Adapter;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.krunal.choosedirectory.ClsGlobal.ThumbnailLoader;
import com.krunal.choosedirectory.R;
import com.krunal.choosedirectory.models.FileInfo;
import java.util.ArrayList;
import java.util.List;
public class FolderAdapter extends BaseListAdapter<FileInfo, FolderAdapter.ViewHolder>
{
    private int itemsSelected = 0;
    private final ThumbnailLoader thumbnailLoader;
    public FolderAdapter(Context context)
    {
        super(context, R.layout.row_file);
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        this.thumbnailLoader = new ThumbnailLoader(context.getResources());
    }
    @Override
    protected ViewHolder viewHolder(View view)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return new ViewHolder(view);
    }
    @Override
    protected void fillView(View rowView, ViewHolder viewHolder, FileInfo fileInfo)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        viewHolder.name.setText(fileInfo.name());

        if (fileInfo.isDirectory()) {
            int numberOfChildren = fileInfo.numberOfChildren();
            viewHolder.size.setText(getContext().getResources().getQuantityString(R.plurals.itemAmount,
                                    numberOfChildren, numberOfChildren));
            viewHolder.icon.setImageResource(R.drawable.ic_folder);
            viewHolder.extension.setText(null);
            viewHolder.extension.setBackgroundResource(android.R.color.transparent);
        } else {
            viewHolder.size.setText(fileInfo.size());

            if (fileInfo.isImage()) {
                thumbnailLoader.load(fileInfo, viewHolder.icon);
                viewHolder.extension.setText(null);
                viewHolder.extension.setBackgroundResource(android.R.color.transparent);
            } else if (fileInfo.isPdf()) {
                viewHolder.icon.setImageResource(R.drawable.ic_pdf);
                viewHolder.extension.setText(null);
                viewHolder.extension.setBackgroundResource(android.R.color.transparent);
            } else if (fileInfo.isAudio()) {
                viewHolder.icon.setImageResource(R.drawable.ic_audio);
                viewHolder.extension.setText(null);
                viewHolder.extension.setBackgroundResource(android.R.color.transparent);
            } else if (fileInfo.isVideo()) {
                viewHolder.icon.setImageResource(R.drawable.ic_video);
                viewHolder.extension.setText(null);
                viewHolder.extension.setBackgroundResource(android.R.color.transparent);
            } else {
                viewHolder.icon.setImageResource(R.drawable.ic_file);
                String extension = fileInfo.extension();

                if (!extension.isEmpty()) {
                    viewHolder.extension.setText(extension);
                    viewHolder.extension.setBackgroundResource(R.drawable.extension_border);

                    if (extension.length() <= 3) {
                        viewHolder.extension.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                    } else {
                        viewHolder.extension.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                    }
                } else {
                    viewHolder.extension.setText(null);
                    viewHolder.extension.setBackgroundResource(android.R.color.transparent);
                }
            }
        }

        if (fileInfo.isSelected()) {
            rowView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray4));
        } else {
            rowView.setBackgroundColor(0);
        }
    }
    public void updateSelection(boolean itemAdded)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        notifyDataSetChanged();
        itemsSelected += itemAdded ? 1 : -1;
    }
    public void setData(List<FileInfo> list)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        update(list);
        unselectAll();
    }
    public void unselectAll()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        for (int i = 0; i < getCount(); i++) {
            FileInfo fileInfo = getItem(i);

            if (fileInfo != null) {
                fileInfo.select(false);
            }
        }

        itemsSelected = 0;
        notifyDataSetChanged();
    }
    public void selectAll()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        for (int i = 0; i < getCount(); i++) {
            FileInfo fileInfo = getItem(i);

            if (fileInfo != null) {
                fileInfo.select(true);
            }
        }

        itemsSelected = getCount();
        notifyDataSetChanged();
    }
    public boolean isSelectionMode()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return itemsSelected > 0;
    }
    public int itemsSelected()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return itemsSelected;
    }
    public boolean allItemsSelected()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return itemsSelected == getCount();
    }
    public List<FileInfo> selectedItems(boolean onlyFiles)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        List<FileInfo> list = new ArrayList<>();

        for (int i = 0; i < getCount(); i++) {
            FileInfo fileInfo = getItem(i);

            if ((fileInfo != null) && fileInfo.isSelected()) {
                if (onlyFiles) {
                    list.addAll(fileInfo.files());
                } else {
                    list.add(fileInfo);
                }
            }
        }

        return list;
    }
    public boolean hasFiles()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        for (int i = 0; i < getCount(); i++) {
            FileInfo fileInfo = getItem(i);

            if ((fileInfo != null) && fileInfo.isSelected()) {
                if (fileInfo.hasFiles()) {
                    return true;
                }
            }
        }

        return false;
    }
    protected static class ViewHolder
    {
        public final TextView name;
        public final TextView size;
        public final TextView extension;
        public final ImageView icon;
        public ViewHolder(View view)
        {
            System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

            this.name = view.findViewById(R.id.name);
            this.size = view.findViewById(R.id.size);
            this.extension = view.findViewById(R.id.extension);
            this.icon = view.findViewById(R.id.icon);
        }
    }
}
