package org.xueliang.commons.exception.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.xueliang.commons.exception.BaseException;
import org.xueliang.commons.exception.ServerInternalException;
import org.xueliang.commons.web.JSONResponse;

public class DefaultExceptionHandler extends DefaultHandlerExceptionResolver {

    private static final Logger LOGGER = LogManager.getLogger(DefaultExceptionHandler.class);
    
    protected MessageSourceAccessor messageSource;
    protected MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
        try {
            LOGGER.error("handle error", ex);
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Type", "application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            JSONResponse jsonResponse = new JSONResponse();
            if (ex instanceof BaseException) {
                BaseException exception = (BaseException) ex;
                jsonResponse.addError(exception);
            } else {
                jsonResponse.addError(new ServerInternalException());
            }
            mappingJackson2HttpMessageConverter.getObjectMapper().writeValue(writer, jsonResponse);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOGGER.error("handle write response error", ex);
        }
        return new ModelAndView();
    }

    public MessageSourceAccessor getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSourceAccessor messageSource) {
        this.messageSource = messageSource;
    }

    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        return mappingJackson2HttpMessageConverter;
    }

    public void setMappingJackson2HttpMessageConverter(
            MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        this.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
    }
}
