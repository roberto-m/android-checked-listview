package com.achaeans.androidcheckedlistview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AchaeansCheckedListView extends ListView {

    private ArrayList<String> choiceOptionNames = new ArrayList<>();
    private ArrayList<String> selectedChoices = new ArrayList<>();
    private HashMap<String, Choice> choicesMap = new HashMap<>();
    private int itemLayoutId;
    private LayoutInflater inflater;
    private CheckedListAdapter checkedListAdapter;
    private int markColor;
    private Drawable markSrc;
    private ChoiceMode choiceMode = ChoiceMode.MULTIPLE;

    public enum ChoiceMode {
        SINGLE, MULTIPLE
    }

    public AchaeansCheckedListView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        checkedListAdapter = new CheckedListAdapter();
        setAdapter(checkedListAdapter);
    }

    public AchaeansCheckedListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        init();
    }

    public void notifyDataSetChanged() {
        checkedListAdapter.notifyDataSetChanged();
    }

    public void setSelected(String choiceName, boolean selected) {
        checkedListAdapter.setSelectChoice(choicesMap.get(choiceName), selected);
    }

    public AchaeansCheckedListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(attrs);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AchaeansCheckedListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setAttributes(attrs);
        init();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AchaeansCheckedListView, 0, 0);
        itemLayoutId = typedArray.getResourceId(R.styleable.AchaeansCheckedListView_itemLayoutId, -1);
        markColor = typedArray.getColor(R.styleable.AchaeansCheckedListView_markColor, Integer.MAX_VALUE);
        markSrc = typedArray.getDrawable(R.styleable.AchaeansCheckedListView_markSrc);

        int mode = typedArray.getInt(R.styleable.AchaeansCheckedListView_choiceMode, 0);
        switch (mode) {
            case 0:
                choiceMode = ChoiceMode.MULTIPLE;
                break;
            case 1:
                choiceMode = ChoiceMode.SINGLE;
                break;
        }
        typedArray.recycle();
    }

    public void setOptions(String[] options) {
        for (String option : options) {
            choiceOptionNames.add(option);
            Choice choice = new Choice(option);
            choicesMap.put(option, choice);
        }
        checkedListAdapter.notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedChoices() {
        return selectedChoices;
    }

    public ArrayList<String> getChoiceOptionNames() {
        return choiceOptionNames;
    }

    private class CheckedListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return choiceOptionNames.size();
        }

        @Override
        public Object getItem(int i) {
            return choicesMap.get(choiceOptionNames.get(i));
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ChoiceViewHolder holder;

            if (view == null) {
                view = inflater.inflate(itemLayoutId, null);
                holder = new ChoiceViewHolder();
                holder.choiceTextView = view.findViewById(R.id.choice_tv);
                holder.choiceMarkImageView = view.findViewById(R.id.choice_mark_iv);
                if (markSrc != null) {
                    holder.choiceMarkImageView.setImageDrawable(markSrc);
                }
                if (markColor != Integer.MAX_VALUE) {
                    holder.choiceMarkImageView.setColorFilter(markColor, PorterDuff.Mode.MULTIPLY);
                }
                view.setTag(holder);
            } else {
                holder = (ChoiceViewHolder) view.getTag();
            }

            final Choice choice = (Choice) getItem(i);

            holder.choiceTextView.setText(choice.option);
            final ImageView imageView = holder.choiceMarkImageView;
            imageView.setVisibility(choice.selected ? VISIBLE : GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (choiceMode) {
                        case MULTIPLE:
                            if (selectedChoices.contains(choice.option)) {
                                setSelectChoice(choice, false);
                            } else {
                                setSelectChoice(choice, true);
                            }
                            break;

                        case SINGLE:
                            resetSelections();
                            setSelectChoice(choice, true);
                            notifyDataSetChanged();
                            break;
                    }
                    imageView.setVisibility(choice.selected ? VISIBLE : GONE);
                    choicesMap.put(choice.option, choice);
                }
            });

            return view;
        }

        private void resetSelections() {
            for(String choiceName : choiceOptionNames) {
                choicesMap.get(choiceName).selected = false;
            }
            selectedChoices.clear();
        }

        private void setSelectChoice(Choice choice, boolean selected) {
            if(choice != null) {
                choice.selected = selected;
                if (selected) {
                    selectedChoices.add(choice.option);
                } else {
                    selectedChoices.remove(choice.option);
                }
            }
        }
    }

    private class ChoiceViewHolder {

        public TextView choiceTextView;
        public ImageView choiceMarkImageView;
    }

    private class Choice {
        String option;
        boolean selected;

        public Choice(String option) {
            this.option = option;
        }
    }
}

