package com.blog.mapper;

import com.blog.entity.Note;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
    IPage<Note> selectNotePage(Page<Note> page, @Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Update("UPDATE note SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
}
