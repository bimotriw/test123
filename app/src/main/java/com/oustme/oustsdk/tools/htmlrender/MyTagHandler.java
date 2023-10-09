package com.oustme.oustsdk.tools.htmlrender;

import android.text.Editable;
import android.text.Html;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;

import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 31/10/18.
 */

public class MyTagHandler implements Html.TagHandler {
    int level = 0;
    private LinkedList<Tag> parentList = new LinkedList<Tag>();
    private HashMap<Integer, Integer> levelWiseCounter = new HashMap<Integer, Integer>();

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader)
    {
        if (tag.equalsIgnoreCase("ul") || tag.equalsIgnoreCase("ol"))
        {
            if (opening)
            {
                if (tag.equalsIgnoreCase("ul"))
                {
                    parentList.push(Tag.UL);
                }
                else
                {
                    parentList.push(Tag.OL);
                }
                level++;
            }
            else
            {
                if (!parentList.isEmpty())
                {
                    parentList.pop();

                    //remove counter at that level, in any present.
                    levelWiseCounter.remove(level);
                }
                level--;
                if (level < 0)
                {
                    level = 0;
                }
            }
        }
        else if (tag.equalsIgnoreCase("li"))
        {
            if (opening && level > 0)
            {
                //new line check
                int length = output.toString().length();
                if (length > 0 && (output.toString().charAt(length - 1) == '\n'))
                {
                }
                else
                {
                    output.append("\n");
                }

                //add tabs as per current level of li
                for (int i = 0; i < level; i++)
                {
                    output.append("\t");
                }

                // append dot or numbers based on parent tag
                if (Tag.UL == parentList.peek())
                {
                    output.append("•");
                }
                else
                {
                    //parent is OL. Check current level and retreive counter from levelWiseCounter
                    int counter = 1;
                    if (levelWiseCounter.get(level) == null)
                    {
                        levelWiseCounter.put(level, 1);
                    }
                    else
                    {
                        counter = levelWiseCounter.get(level) + 1;
                        levelWiseCounter.put(level, counter);
                    }
                    output.append(padInt(counter) + ".");
                }

                //trailing tab
                output.append("\t");

            }
        }
    }

    /**
     * Add padding so that all numbers are aligned properly. Currently supports padding from 1-99.
     *
     * @param num
     * @return
     */
    private static String padInt(int num)
    {
        if (num < 10)
        {
            return " " + num;
        }
        return "" + num;
    }

    private enum Tag
    {
        UL, OL
    }
}