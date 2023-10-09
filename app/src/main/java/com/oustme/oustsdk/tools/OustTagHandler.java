package com.oustme.oustsdk.tools;

import android.text.Editable;
import android.text.Html;

import com.oustme.oustsdk.tools.htmlrender.NumberSpan;
import com.oustme.oustsdk.tools.htmlrender.OustBulletSpan;

import org.xml.sax.XMLReader;

/**
 * Created by shilpysamaddar on 11/03/17.
 */

public class OustTagHandler implements Html.TagHandler{
    private int mListItemCount = 0;
    private String myTag;
    private String lastTag;
    private boolean lastOpening=false;
    private Editable lastOutput;

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if(tag.equals("OUSTVINULO")){
            if(opening){
                output.append("OUSTVINULO");
            }else {
                output.append("OUSTVINULC");
            }
        } else if(tag.equals("OUSTVINOLO")){
            if(opening){
                output.append("OUSTVINOLO");
            }else {
                output.append("OUSTVINOLC");
            }
        } else if(tag.equals("OUSTVINLIO")){
            if(opening){
                output.append("OUSTVINLIO");
            }else {
                output.append("OUSTVINLIC");
            }
        }
    }

    //    @Override
//    public void handleTag(final boolean opening, final String tag, Editable output, final XMLReader xmlReader) {
//        lastTag=tag;
//        if (tag.equals("MYOUSTUL")) {
//            if (opening) {
//                mListItemCount=0;
//                this.myTag=tag;
//            } else {
//                if ((output != null)){
//                    handleListUlTagA(output);
//                    output.append("\n");
//                }
//                mListItemCount=0;
//                this.myTag="";
//            }
//        }else if (tag.equals("MYOUSTOL") ) {
//            if (opening) {
//                mListItemCount=0;
//                this.myTag=tag;
//            } else {
//                if ((output != null)){
//                    handleListOlTagA(output);
//                    output.append("\n");
//                }
//                mListItemCount=0;
//                this.myTag="";
//            }
//        } else if (tag.equals("MYOUSTLI")) {
//            if(opening) {
//                if(myTag.equals("MYOUSTUL")){
//                    if ((output != null)) {
//                        handleListUlTag(output);
//                    }
//                }else if(myTag.equals("MYOUSTOL")){
//                    if ((output != null)) {
//                        handleListOlTag(output);
//                    }
//                }
//            }
//        }
//        lastOutput=output;
//    }

    private void handleListUlTag(Editable output) {
        if(myTag.equals("MYOUSTUL")){
            mListItemCount++;
            if(output.length()!=0)
                output.append("\n");
            String[] split = output.toString().split("\n");
            int start = 0;
            if (split.length != 1) {
                int lastIndex = split.length - 1;
                start = output.length() - split[lastIndex].length() - 1;
            }
            if((mListItemCount-1)!=0) {
                output.setSpan(new OustBulletSpan(), start, output.length(), 0);
            }
        }

    }

    private void handleListUlTagA(Editable output) {
        if(myTag.equals("MYOUSTUL")){
            mListItemCount++;
            if(output.length()!=0)
                output.append("\n",lastOutput.length()-1,lastOutput.length());
            String[] split = output.toString().split("\n");
            int start = 0;
            if (split.length != 1) {
                int lastIndex = split.length -2;
                start = output.length() - split[lastIndex].length() - 1;
            }
            if((mListItemCount-1)!=0) {
                output.setSpan(new OustBulletSpan(), start, lastOutput.length(), 0);
            }
        }

    }

    private void handleListOlTag(Editable output) {
        if (myTag.equals("MYOUSTOL")) {
            mListItemCount++;
            if(output.length()!=0)
                output.append("\n");
            String[] split = output.toString().split("\n");
            int start = 0;
            if (split.length != 1) {
                int lastIndex = split.length - 1;
                start = output.length() - split[lastIndex].length() - 1;
            }
            if((mListItemCount-1)!=0) {
                output.setSpan(new NumberSpan(mListItemCount - 1), start, output.length(), 0);
            }
        }
    }

    private void handleListOlTagA(Editable output) {
        if (myTag.equals("MYOUSTOL")) {
            mListItemCount++;
            if(output.length()!=0)
                output.append("\n",lastOutput.length()-1,lastOutput.length());
            //output.append("\n");
            String[] split = output.toString().split("\n");
            int start = 0;
            if (split.length != 1) {
                int lastIndex = split.length - 2;
                start = output.length() - split[lastIndex].length() - 1;
            }
            if((mListItemCount-1)!=0) {
                output.setSpan(new NumberSpan(mListItemCount - 1), start, lastOutput.length(), 0);
            }
        }
    }


//
//    private void handleListTag(Editable output) {
//
//        if (mListParents.lastElement().equals("MYOUSTUL")) {
//            mListItemCount=mListCounter.lastElement()+1;
//            if(output.length()!=0)
//                output.append("\n");
//            String[] split = output.toString().split("\n");
//            int start = 0;
//            if (split.length != 1) {
//                int lastIndex = split.length - 1;
//                start = output.length() - split[lastIndex].length() - 1;
//            }
//            if((mListItemCount-1)!=0) {
//                output.setSpan(new OustBulletSpan(), start, output.length(), 0);
//            }
//            mListCounter.removeElementAt(mListCounter.size()-1);
//            mListCounter.add(mListCounter.size(), mListItemCount);
//        } else if (mListParents.lastElement().equals("MYOUSTOL")) {
//            mListItemCount=mListCounter.lastElement()+1;
//            if(output.length()!=0)
//                output.append("\n");
//            String[] split = output.toString().split("\n");
//            int start = 0;
//            if (split.length != 1) {
//                int lastIndex = split.length - 1;
//                start = output.length() - split[lastIndex].length() - 1;
//            }
//            if((mListItemCount-1)!=0) {
//                output.setSpan(new NumberSpan(mListItemCount - 1), start, output.length(), 0);
//            }
//            //output.append( mListItemCount + ". ");
//            mListCounter.removeElementAt(mListCounter.size()-1);
//            mListCounter.add(mListCounter.size(), mListItemCount);
//        }
//    }


//    @Override
//    public void handleTag(final boolean opening, final String tag, Editable output, final XMLReader xmlReader) {
//        if (tag.equals("MYOUSTUL") || tag.equals("MYOUSTOL") ) {
//            if (opening) {
//                mListParents.add(mListParents.size(),tag);
//                mListCounter.add(mListCounter.size(),  0);
//            } else {
//                if ((output != null)){
//                    handleListTag(output);
//                    output.append("\n");
//                }
//                mListParents.removeElementAt(mListParents.size()-1);
//                mListCounter.removeElementAt(mListCounter.size()-1);
//            }
//        } else if (tag.equals("MYOUSTLI")) {
//            if(opening) {
//                if ((output != null)) {
//                    handleListTag(output);
//                }
//            }
//        }
//    }


}
