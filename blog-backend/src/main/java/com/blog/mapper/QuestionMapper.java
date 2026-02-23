package com.blog.mapper;

import com.blog.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    IPage<Question> selectQuestionPage(Page<Question> page, @Param("keyword") String keyword);

    @Update("UPDATE question SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
}
