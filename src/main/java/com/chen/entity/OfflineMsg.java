package com.chen.entity;

import java.util.Date;

import lombok.Data;

@Data
public class OfflineMsg {
	private Integer id;

	private Integer msg_id;

	private Integer msg_type;

	private Integer msg_status;

	private String sender_id;

	private String receiver_id;

	private String text;

	private String extra;

	private String web_url;

	private Double latitude;

	private Double longitude;

	private String sender_avatar;

	private String receiver_avatar;

	private String sender_name;

	private String receiver_name;

	private String thumbnail;

	private Integer duration;

	private Integer create_time;

	private Date create_date;

}