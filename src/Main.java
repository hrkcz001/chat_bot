import org.telegram.telegrambots.*;
  import org.telegram.telegrambots.bots.*;
  import org.telegram.telegrambots.facilities.*;
    import org.telegram.telegrambots.facilities.filedownloader.*;
    import org.telegram.telegrambots.facilities.proxysocketfactorys.*;
  import org.telegram.telegrambots.updatesreceivers.*;
  import org.telegram.telegrambots.util.*;
import org.telegram.telegrambots.meta.*;
  import org.telegram.telegrambots.meta.bots.*;
  import org.telegram.telegrambots.meta.api.*;
    import org.telegram.telegrambots.meta.api.interfaces.*;
    import org.telegram.telegrambots.meta.api.methods.*;
      import org.telegram.telegrambots.meta.api.methods.games.*;
      import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
      import org.telegram.telegrambots.meta.api.methods.pinnedmessages.*;
      import org.telegram.telegrambots.meta.api.methods.polls.*;
      import org.telegram.telegrambots.meta.api.methods.send.*;
      import org.telegram.telegrambots.meta.api.methods.stickers.*;
      import org.telegram.telegrambots.meta.api.methods.updates.*;
      import org.telegram.telegrambots.meta.api.methods.updatingmessages.*;
    import org.telegram.telegrambots.meta.api.objects.*;
      import org.telegram.telegrambots.meta.api.objects.games.*;
      import org.telegram.telegrambots.meta.api.objects.inlinequery.*;
        import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.*;
          import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.serialization.*;
        import org.telegram.telegrambots.meta.api.objects.inlinequery.result.*;
          import org.telegram.telegrambots.meta.api.objects.inlinequery.result.cached.*;
          import org.telegram.telegrambots.meta.api.objects.inlinequery.result.chached.*;
          import org.telegram.telegrambots.meta.api.objects.inlinequery.result.serialization.*;
      import org.telegram.telegrambots.meta.api.objects.media.*;
      import org.telegram.telegrambots.meta.api.objects.passport.*;
      import org.telegram.telegrambots.meta.api.objects.payments.*;
      import org.telegram.telegrambots.meta.api.objects.polls.*;
      import org.telegram.telegrambots.meta.api.objects.replykeyboard.*;
        import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.*;
        import org.telegram.telegrambots.meta.api.objects.replykeyboard.serialization.*;
      import org.telegram.telegrambots.meta.api.objects.stickers.*;
  import org.telegram.telegrambots.meta.exceptions.*;
  import org.telegram.telegrambots.meta.generics.*;
  import org.telegram.telegrambots.meta.updateshandlers.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import org.bukkit.configuration.file.YamlConfiguration;
import java.net.*;

public class Main {
      public static void main(String[] args) throws Exception{

          java.io.File f;

          YamlConfiguration stickers = new YamlConfiguration();

          f = new java.io.File ("stickers.yml");
          if (f.exists()) stickers.load(f);

          YamlConfiguration admins = new YamlConfiguration();

          f = new java.io.File ("admins.yml");
          if (f.exists()) admins.load(f);

          YamlConfiguration saves = new YamlConfiguration();

          f = new java.io.File ("saves.yml");
          if (f.exists()) saves.load(f);

          int[] realTime = new int[17];

          f = new java.io.File ("real_time.txt");
          if (f.exists()){

            Scanner sc = new Scanner(f);
            int i = 0;

            while(sc.hasNext()){

              realTime[i] = Integer.parseInt(sc.nextLine());
              i++;

            }

            sc.close();

          }

          String[] events = new String[17];

          f = new java.io.File("events.txt");
          if (f.exists()){

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            int i = 0;

            String line;

            while((line = br.readLine()) != null){

              events[i] = line;
              i++;

            }

            br.close();

          }


          ApiContextInitializer.init();

          TelegramBotsApi botsApi = new TelegramBotsApi();

          try {
              botsApi.registerBot(new ChatBot(stickers, admins, saves, realTime, events));
          } catch (TelegramApiException e) {
              e.printStackTrace();
          }

      }
}

class ChatBot extends TelegramLongPollingBot {

  final long CHAT_ID = -1001351968363L;

  YamlConfiguration stickers = new YamlConfiguration();
  YamlConfiguration admins = new YamlConfiguration();
  YamlConfiguration saves = new YamlConfiguration();
  ArrayList<Integer> admin_requests = new ArrayList();
  ArrayList users = new ArrayList();
  String word = "";
  Boolean waiting = false;
  long user_waiting = 0;
  Boolean start_waiting = false;
  int[] realTime = new int[1];
  String[] events;

    public ChatBot(YamlConfiguration stickers, YamlConfiguration admins, YamlConfiguration saves, int[] realTime, String[] events){

      super();
      this.stickers = stickers;
      this.admins = admins;
      this.saves = saves;
      this.realTime = realTime;
      this.events = events;

      new EveryDay(this.realTime, this.events, this).start();

    }

    public void onUpdateReceived(Update update){
      try{
        onUpdate(update);
      }
      catch(Exception e){

        e.printStackTrace();

      }
    }

    public boolean onUpdate(Update update) throws Exception{

        start_waiting = false;

        Boolean z = false;

        try{

          z = update.getMessage().getChatId() == CHAT_ID;

        }catch(NullPointerException e){z = false;}

        if(z){

        String request = update.getMessage().getText();

        if(request != null){

          if(request.equals("!on")){

            if(!users.contains(update.getMessage().getFrom().getId())){

              users.add(update.getMessage().getFrom().getId());

              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Включено");
              execute(sm);

            }
            else{

              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Уже активно");
              execute(sm);

            }

          }

          if(request.equals("!off")){

            if(users.contains(update.getMessage().getFrom().getId())){

              users.remove(update.getMessage().getFrom().getId());

              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Отключено");
              execute(sm);

            }
            else{

              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Вас нет в списке активировавших");
              execute(sm);

            }

          }

        }

        if(users.contains(update.getMessage().getFrom().getId())){

        if(request != null){

          if(request.equals("@oleg_mongobot")){

            DeleteMessage dm = new DeleteMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());
            execute(dm);

            return true;

          }

          if(request.split(" +")[0].equals("@oleg_mongobot")){

            request = request.replace("@oleg_mongobot ", "");

          }

          switch(request.split(" +")[0]){

            case "!set": if(!waiting){
                            if(request.split(" +").length == 2){

                              if(!(request.split(" +")[1].charAt(0) == '!')){
                                if(!stickers.contains(request.split(" +")[1])){

                                  waiting = true;
                                  user_waiting = update.getMessage().getFrom().getId();
                                  word = request.split(" +")[1];

                                  SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Ожидаю");
                                  execute(sm);

                                  start_waiting = true;

                                }
                                else{

                                  SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Тег-слово уже занято");
                                  execute(sm);

                                }
                              }
                              else{

                                SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Ключевые слова не могут быть использованы");
                                execute(sm);

                              }

                            }
                            else{

                              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нужен 1 аргумент");
                              execute(sm);

                            }
                         }
                         else{

                           SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Вначале отмените предедущий запрос");
                           execute(sm);

                         }
                         return true;
            case "!cancel": if(waiting){

                              if(request.split(" +").length == 1){

                                if((user_waiting == update.getMessage().getFrom().getId()) || (admins.contains(Long.toString(update.getMessage().getFrom().getId())))){

                                  waiting = false;
                                  user_waiting = 0;
                                  word = "";

                                  SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Отменено");
                                  execute(sm);

                                }
                                else{

                                  SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Вы не создатель запроса");
                                  execute(sm);

                                }

                              }else{

                                SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Аргументы не требуются");
                                execute(sm);

                              }

                            }else{

                              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет текущих запросов");
                              execute(sm);

                            }
                            return true;

              case "!rmv":  if(request.split(" +").length == 2){

                              Boolean f = false;

                              GetChatMember gcm = new GetChatMember();
                              gcm.setChatId(update.getMessage().getChatId());
                              gcm.setUserId(update.getMessage().getFrom().getId());
                              ChatMember cm = execute(gcm);
                              User ucm = cm.getUser();

                              if((admins.contains("@" + ucm.getUserName())) && (admins.get("@" + ucm.getUserName()) != null)){

                                f = true;

                              }
                              else{

                                if(cm.getStatus().equals("creator")){

                                  f = true;

                                }

                              }

                              if(f){

                                if(stickers.contains(request.split(" +")[1])){

                                  stickers.set(request.split(" +")[1], null);

                                  java.io.File file = new java.io.File ("stickers.yml");
                                  if (file.exists()) file.delete();

                                  stickers.save(file);

                                  SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Успешно");
                                  execute(sm);

                                }
                                else{

                                  SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Тег-слова не существует");
                                  execute(sm);

                                }

                              }
                              else{

                                SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет прав на выполнение данного действия");
                                execute(sm);

                              }

                            }
                            else{

                              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нужен 1 аргумент");
                              execute(sm);

                            }

                            return true;

              case "!request_for_admin":  GetChatMember gcm = new GetChatMember();
                                          gcm.setChatId(update.getMessage().getChatId());
                                          gcm.setUserId(update.getMessage().getFrom().getId());
                                          ChatMember cm = execute(gcm);
                                          User ucm = cm.getUser();

                                          if(!((cm.getStatus().equals("creator")) || ((admins.contains("@" + ucm.getUserName())) && (admins.get("@" + ucm.getUserName()) != null)))){

                                            if(cm.getStatus().equals("administrator")){

                                              admin_requests.add(update.getMessage().getFrom().getId());

                                              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Запрос создан");
                                              execute(sm);

                                            }else{

                                              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Вы должны быть администратором группы");
                                              execute(sm);

                                            }

                                          }
                                          else{

                                            SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Вы уже администратор");
                                            execute(sm);

                                          }

                                          return true;

                case "!request_check":  gcm = new GetChatMember();
                                        gcm.setChatId(update.getMessage().getChatId());
                                        gcm.setUserId(update.getMessage().getFrom().getId());
                                        cm = execute(gcm);

                                        if(cm.getStatus().equals("creator")){
                                          if(!admin_requests.isEmpty()){
                                            gcm = new GetChatMember();
                                            gcm.setChatId(update.getMessage().getChatId());
                                            gcm.setUserId(admin_requests.get(0));
                                            cm = execute(gcm);
                                            ucm = cm.getUser();

                                            String firstName = ucm.getFirstName();
                                            String lastName = ucm.getLastName();

                                            if(firstName == null){
                                              firstName = "";
                                            }
                                            if(lastName == null){
                                              lastName = "";
                                            }

                                            SendMessage sm = new SendMessage(update.getMessage().getChatId(), "@" + ucm.getUserName() + "\n" + firstName + " " + lastName);
                                            execute(sm);
                                          }
                                          else{

                                            SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет активных заявок");
                                            execute(sm);

                                          }
                                        }
                                        else{

                                          SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет прав на выполнение данного действия");
                                          execute(sm);

                                        }

                                        return true;

              case "!request_count":  gcm = new GetChatMember();
                                      gcm.setChatId(update.getMessage().getChatId());
                                      gcm.setUserId(update.getMessage().getFrom().getId());
                                      cm = execute(gcm);

                                      if(cm.getStatus().equals("creator")){

                                        SendMessage sm = new SendMessage(update.getMessage().getChatId(), Integer.toString(admin_requests.size()));
                                        execute(sm);

                                      }
                                      else{

                                        SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет прав на выполнение данного действия");
                                        execute(sm);

                                      }

                                      return true;

              case "!request_accept": gcm = new GetChatMember();
                                      gcm.setChatId(update.getMessage().getChatId());
                                      gcm.setUserId(update.getMessage().getFrom().getId());
                                      cm = execute(gcm);

                                      if(cm.getStatus().equals("creator")){
                                        if(!admin_requests.isEmpty()){

                                          gcm = new GetChatMember();
                                          gcm.setChatId(update.getMessage().getChatId());
                                          gcm.setUserId(admin_requests.get(0));
                                          cm = execute(gcm);
                                          ucm = cm.getUser();

                                          admins.set("@" + ucm.getUserName(), 1);

                                          java.io.File f = new java.io.File ("admins.yml");
                                          if (f.exists()) f.delete();

                                          admins.save(f);

                                          admin_requests.remove(0);

                                          SendMessage sm = new SendMessage(update.getMessage().getChatId(), "@" + ucm.getUserName() + " Заявка принята");
                                          execute(sm);

                                        }else{

                                          SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет активных заявок");
                                          execute(sm);

                                        }

                                      }
                                      else{

                                        SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет прав на выполнение данного действия");
                                        execute(sm);

                                      }

                                      return true;

                case "!request_refuse": gcm = new GetChatMember();
                                        gcm.setChatId(update.getMessage().getChatId());
                                        gcm.setUserId(update.getMessage().getFrom().getId());
                                        cm = execute(gcm);

                                        if(cm.getStatus().equals("creator")){
                                          if(!admin_requests.isEmpty()){

                                            gcm = new GetChatMember();
                                            gcm.setChatId(update.getMessage().getChatId());
                                            gcm.setUserId(admin_requests.get(0));
                                            cm = execute(gcm);
                                            ucm = cm.getUser();

                                            admin_requests.remove(0);

                                            SendMessage sm = new SendMessage(update.getMessage().getChatId(), "@" + ucm.getUserName() + " Заявка отклонена");
                                            execute(sm);
                                          }else{

                                            SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет активных заявок");
                                            execute(sm);

                                          }

                                        }
                                        else{

                                          SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет прав на выполнение данного действия");
                                          execute(sm);

                                        }

                                        return true;

             case "!admin_delete": if(request.split(" +").length == 2){

                                      gcm = new GetChatMember();
                                      gcm.setChatId(update.getMessage().getChatId());
                                      gcm.setUserId(update.getMessage().getFrom().getId());
                                      cm = execute(gcm);

                                      if(cm.getStatus().equals("creator")){

                                        if(admins.contains(request.split(" +")[1])){

                                          admins.set(request.split(" +")[1], null);

                                          java.io.File f = new java.io.File ("admins.yml");
                                          if (f.exists()) f.delete();

                                          admins.save(f);

                                          SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Успешно");
                                          execute(sm);

                                        }
                                        else{

                                          SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Не является админом");
                                          execute(sm);

                                        }

                                      }

                                    }
                                    else{

                                      SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нужен 1 аргумент");
                                      execute(sm);

                                    }

                                    return true;

             case "!update_time": java.io.File time_file = new java.io.File ("time.txt");
                                  FileWriter fw = new FileWriter("real_time.txt", false);

                                  Scanner sc = new Scanner(time_file);

                                  Boolean fstCheck = true;

                                  while(sc.hasNext()){

                                    if(!fstCheck){
                                      fw.append("\n");
                                    }else{
                                      fstCheck = false;
                                    }
                                    fw.append(Integer.toString((Integer.parseInt(sc.next()) * 60 + Integer.parseInt(sc.next())) * 60));
                                    sc.nextLine();

                                  }

                                  fw.flush();
                                  sc.close();

                                  java.io.File f = new java.io.File ("real_time.txt");

                                  sc = new Scanner(f);
                                  int i = 0;
                                  while(sc.hasNext()){

                                    realTime[i] = sc.nextInt();
                                    sc.nextLine();
                                    i++;

                                  }

                                  sc.close();

                                  return true;

                case "!save":     if(request.split(" +").length == 2){

                                      if(update.getMessage().isReply()){

                                        if(!saves.contains(request.split(" +")[1])){

                                          saves.set(request.split(" +")[1], Long.toString(update.getMessage().getReplyToMessage().getMessageId()));
                                          java.io.File file = new java.io.File ("saves.yml");
                                          if (file.exists()) file.delete();

                                          saves.save(file);

                                          SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Успешно");
                                          execute(sm);

                                        }
                                        else{
                                          SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Тег-слово уже занято");
                                          execute(sm);
                                        }
                                      }
                                      else{
                                        SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Отправьте это в ответ на сохраняемое сообщение");
                                        execute(sm);
                                      }
                                  }
                                  else{
                                    SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нужен 1 аргумент");
                                    execute(sm);
                                  }

                                  return true;

                case "!load":     if(request.split(" +").length == 2){
                                    if((saves.contains(request.split(" +")[1])) && (saves.get(request.split(" +")[1]) != null)){

                                        ForwardMessage fm = new ForwardMessage();
                                        fm.setChatId(CHAT_ID);
                                        fm.setFromChatId(CHAT_ID);
                                        fm.setMessageId(Integer.parseInt((String)saves.get(request.split(" +")[1])));
                                        execute(fm);

                                    }
                                    else{
                                        SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Тег-слово не найдено");
                                        execute(sm);
                                    }
                                  }
                                  else{
                                    SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нужен 1 аргумент");
                                    execute(sm);
                                  }

                                  return true;

                case "!delete_save":  if(request.split(" +").length == 2){

                                        Boolean flag = false;

                                        gcm = new GetChatMember();
                                        gcm.setChatId(update.getMessage().getChatId());
                                        gcm.setUserId(update.getMessage().getFrom().getId());
                                        cm = execute(gcm);
                                        ucm = cm.getUser();

                                        if((admins.contains("@" + ucm.getUserName())) && (admins.get("@" + ucm.getUserName()) != null)){

                                          flag = true;

                                        }
                                        else{

                                          if(cm.getStatus().equals("creator")){

                                            flag = true;

                                          }

                                        }

                                        if(flag){

                                          if(saves.contains(request.split(" +")[1])){

                                            saves.set(request.split(" +")[1], null);
                                            java.io.File file = new java.io.File ("saves.yml");
                                            if (file.exists()) file.delete();

                                            saves.save(file);

                                            SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Успешно");
                                            execute(sm);

                                          }
                                          else{
                                              SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Тег-слово не найдено");
                                              execute(sm);
                                          }

                                        }
                                        else{

                                            SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нет прав на выполнение данного действия");
                                            execute(sm);

                                        }

                                      }
                                      else{

                                        SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Нужен 1 аргумент");
                                        execute(sm);

                                      }

                                      return true;


         }

         if((!start_waiting) && (update.getMessage().getFrom().getId() == user_waiting)){

           DeleteMessage dm = new DeleteMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());
           execute(dm);

           return true;

         }

         if((stickers.contains(request)) && (stickers.get(request) != null)){

           SendSticker ss = new SendSticker();
           //ss.replyToMessageId(update.getMessage().getMessageId());
           ss.setChatId(update.getMessage().getChatId());
           ss.setSticker((String)stickers.get(request));
           InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
           java.util.List<InlineKeyboardButton> arlIKB = new ArrayList<>();
           arlIKB.add(new InlineKeyboardButton(update.getMessage().getFrom().getFirstName()).setSwitchInlineQueryCurrentChat(request));
           java.util.List<java.util.List<InlineKeyboardButton>> rowList= new ArrayList<>();
           rowList.add(arlIKB);
           ikm.setKeyboard(rowList);
           ss.setReplyMarkup(ikm);
           execute(ss);

           /*HttpURLConnection con = (HttpURLConnection) new URL("https://api.telegram.org/bot980161163:AAGrNQtUmwv4tbhKn6lOsMWdb7o86U7rT7I/sendSticker?chat_id=" + Long.toString(update.getMessage().getChatId()) + "&sticker=" + (String)stickers.get(request) + "&reply_to_message_id=" + update.getMessage().getMessageId()).openConnection();
           con.setRequestMethod("GET");
           con.setRequestProperty("User-Agent", "Mozilla/5.0");
           con.getResponseCode();*/

           DeleteMessage dm = new DeleteMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());
           execute(dm);

           return true;

         }

        }
        else{

        Sticker sticker_request = update.getMessage().getSticker();

        if(update.getMessage().getFrom().getId() == user_waiting){

          if(sticker_request != null){

            stickers.set(word, sticker_request.getFileId());

            java.io.File f = new java.io.File ("stickers.yml");
            if (f.exists()) f.delete();

            stickers.save(f);

            waiting = false;
            user_waiting = 0;

            SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Успешно");
            execute(sm);

          }

        }
      }

    }

      }
      else{

        try{

          SendMessage sm = new SendMessage(update.getMessage().getChatId(), "Неверный чат");
          execute(sm);

        }catch(NullPointerException e){}

      }

      return true;

    }

    public String getBotUsername() {

        return "Олег Монгол";

    }

    public String getBotToken() {

        return "980161163:AAGrNQtUmwv4tbhKn6lOsMWdb7o86U7rT7I";

    }

}

class EveryDay extends Thread{

  final long CHAT_ID = -1001351968363L;

  int[] realTime;
  ChatBot bot;
  String[] events;

  public EveryDay(int[] realTime, String[] events, ChatBot bot){
    this.realTime = realTime;
    this.bot = bot;
    this.events = events;
  }

  public void run(){

    try{

    SetChatDescription scd = new SetChatDescription(CHAT_ID, events[events.length - 1]);
    bot.execute(scd);

    Thread.sleep(realTime[0] * 1000);

    for(int i = 0; true; i++){

      scd = new SetChatDescription(CHAT_ID, events[i]);
      bot.execute(scd);

      Thread.sleep((realTime[i + 1] - realTime[i]) * 1000);

      if(i == (realTime.length - 2)){

        scd = new SetChatDescription(CHAT_ID, events[i]);
        bot.execute(scd);

        Thread.sleep(realTime[0] * 1000);

        i = -1;

      }

    }

    }
    catch(Exception e){}
  }

}
