package com.xz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xz.entity.Apply;
import com.xz.entity.ApplyOVUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApplyMapper extends BaseMapper<Apply> {
    @Select("select ta.id,ta.state,ta.uid,ta.to_uid,ta.create_time,tu.username,tu.avatar from t_apply ta,t_user tu where ta.uid=tu.uid and ta.to_uid='${uid}' order by ta.create_time desc")
    public List<ApplyOVUser> getMyApply(String uid);
}
