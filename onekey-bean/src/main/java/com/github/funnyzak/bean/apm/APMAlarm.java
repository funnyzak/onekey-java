package com.github.funnyzak.bean.apm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.funnyzak.bean.PotatoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;

import java.util.Date;


@Table("potato_apm_alarm")
@Comment("性能告警表")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableMeta("{mysql-charset:'utf8mb4'}")
public class APMAlarm extends PotatoEntity {

	private static final long serialVersionUID = 1L;

	public static enum Type {
		SLOW, MEM, DISK, CPU, NETWORK;
	}

	@Name
	@Column("alarm_code")
	@Comment("报警信息编号")
	private String code = R.UU32();

	@Column("alarm_type")
	@Comment("报警类型")
	private Type type;

	@Column("alarm_time")
	@Comment("报警时间")
	private Date alarmTime = Times.now();

	@Column("alarm_msg")
	@Comment("报警消息")
	@ColDefine(width = 250)
	private String msg;

	@Column("alarm_ip")
	@Comment("报警 ip")
	private String ip;

	@Column("alarm_title")
	@Comment("报警标题")
	private String title;

	@Column("alarm_device")
	@Comment("报警设备")
	private String device;

	@Column("alarm_usage")
	@Comment(" 设备使用情况")
	private double usage;

	@Column("alarm_alarm_point")
	@Comment("设备告警点")
	private Integer alarm;

}
