package com.kuklin.interview_telegram_service.integrations;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "telegramClient", url = "https://api.telegram.org")
public interface TelegramFeignClient {

    @GetMapping("/bot{token}/getFile")
    @ResponseBody
    String getFileRaw(@PathVariable("token") String botToken,
                      @RequestParam("file_id") String fileId);

    @GetMapping("/file/bot{token}/{filePath}")
    @ResponseBody
    byte[] downloadFile(@PathVariable("token") String botToken,
                        @PathVariable("filePath") String filePath);
}
