package com.baidu.shop.utils;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName HighlightUtil
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/3
 * @Version V1.0
 **/
public class HighlightUtil {

    public static HighlightBuilder getHighlightBuilder(String ...field){
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        Arrays.asList(field).stream().forEach(field1->{
            highlightBuilder.field(field1);
            highlightBuilder.preTags("<font style='color:red'>");
            highlightBuilder.postTags("</font>");
        });
        return highlightBuilder;
    }

    public static <T> List<T> getHighlightList(List<SearchHit<T>> searchHits){
            return  searchHits.stream().map(searchHit->{
                T content = searchHit.getContent();
                Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
                highlightFields.forEach((key,value)->{
                    try {
                        Method method = content.getClass().getMethod("set" + firstCharUpper(key), String.class);
                        method.invoke(content,value.get(0));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });
                return content;
            }).collect(Collectors.toList());
    }

    public static String firstCharUpper(String str){
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }
}
