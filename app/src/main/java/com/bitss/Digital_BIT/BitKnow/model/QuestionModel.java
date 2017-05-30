package com.bitss.Digital_BIT.BitKnow.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Junhao Zhou 2017/5/24
 */

public class QuestionModel implements Serializable {

  public int id;

  public OwnerModel owner;

  public String title;

  public String content;

  public String iconUrl;

  public String date;

  public int answerNum;

  public List<TagModel> tags;
}
