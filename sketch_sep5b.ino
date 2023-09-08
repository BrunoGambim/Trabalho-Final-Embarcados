#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

#include <WiFiUdp.h>
#include <string.h>

#include <SoftwareSerial.h>
#include <PN532_SWHSU.h>
#include <PN532.h>

#define UDP_PORT 4210

#define PACKET_LEN 1000

#define OPEN 0
#define CLOSED 1

#define NOT_RUNNING 0
#define RUNNING 1

int udp_server_status = NOT_RUNNING;
bool lock_status = CLOSED;

WiFiUDP UDP;
char packet[PACKET_LEN];
char reply[] = "Packet received!";

IPAddress broadcast_ip = IPAddress(192, 168, 15, 255);

const char *ssid = "AC-ESP8266";
const char *password = "987654321";

ESP8266WebServer server(80);

bool LEDstatus = LOW;

typedef struct user_node {
  char *user_id;
  char *user_name;
  int has_access;
  struct user_node *next_node;
} USER_NODE;

USER_NODE* user_list = NULL;
int user_list_len = 0;

USER_NODE* create_node(char *user_id, char *user_name){
  USER_NODE* new_node;
  new_node = (USER_NODE*) calloc(1, sizeof(USER_NODE));
  new_node->user_name = user_name;
  new_node->user_id = user_id;
  new_node->has_access = 0;
  new_node->next_node = NULL;
  user_list_len++;
  return new_node;
}

USER_NODE* insert_node(char *user_id, char *user_name){
  USER_NODE* aux;
  if(user_list == NULL){
    user_list = create_node(user_id, user_name);
    return user_list;
  }else{
    aux = user_list;
    while(aux->next_node != NULL){
      if(strcmp(aux->user_id, user_id) == 0){
        free(aux->user_name);
        free(user_id);
        aux->user_name = user_name;
        return aux;
      }
      aux = aux->next_node;
    }
    if(strcmp(aux->user_id, user_id) == 0){
      free(aux->user_name);
      free(user_id);
      aux->user_name = user_name;
      return aux;
    }
    aux->next_node = create_node(user_id, user_name);
    return aux->next_node;
  }
}

void remove_node(char* user_name){
  USER_NODE *aux, *aux2;
  if(user_list != NULL && strcmp(user_list->user_name, user_name) == 0){
    aux = user_list;
    user_list = user_list->next_node;
    free(aux->user_id);
    free(aux->user_name);
    free(aux);
    user_list_len--;
  }
  if(user_list == NULL){
    free(user_name);
    return;
  }
  aux = user_list;
  while(aux->next_node != NULL){
    if(strcmp(aux->next_node->user_name, user_name) == 0){
      aux2 = aux->next_node;
      aux->next_node = aux->next_node->next_node;
      free(aux2->user_id);
      free(aux2->user_name);
      free(aux2);
      user_list_len--;
    }else{
      aux = aux->next_node;
    }
  }
  free(user_name);
}

USER_NODE* find_node(char *user_id){
  USER_NODE* aux;
  aux = user_list;
  while(aux != NULL){
    if(strcmp(aux->user_id, user_id) == 0){
      free(user_id);
      return aux;
    }
    aux = aux->next_node;
  }
  free(user_id);
  return NULL;
}

int has_access(char *user_id){
  USER_NODE* user;
  user = find_node(user_id);
  if(user == NULL){
    return 0;
  }
  return user->has_access;
}

void give_access(char *user_name){
  USER_NODE* aux;
  aux = user_list;
  while(aux != NULL){
    if(strcmp(aux->user_name, user_name) == 0){
      aux->has_access = 1;
    }
    aux = aux->next_node;
  }
  free(user_name);
}

#define MAX_PACKAGE_PARTS 5
#define MAX_PACKAGE_PART_LENGTH 200

#define FIND_BOARD_PACKAGE 1
#define FIND_BOARD_PACKAGE_COMPONENTS 2
#define FIND_BOARD_PACKAGE_ID 0
#define FIND_BOARD_PACKAGE_NAME 1

#define ADD_BOARD_PACKAGE 2
#define ADD_BOARD_PACKAGE_COMPONENTS 5
#define ADD_BOARD_PACKAGE_BOARD_NAME 0
#define ADD_BOARD_PACKAGE_WIFI_NAME 1
#define ADD_BOARD_PACKAGE_WIFI_PASS 2
#define ADD_BOARD_PACKAGE_ADMIN_ID 3
#define ADD_BOARD_PACKAGE_ADMIN_NAME 4

#define OPEN_PACKAGE 3
#define OPEN_PACKAGE_COMPONENTS 2
#define OPEN_PACKAGE_ID 0
#define OPEN_PACKAGE_NAME 1

#define CLOSE_PACKAGE 4
#define CLOSE_PACKAGE_COMPONENTS 2
#define CLOSE_PACKAGE_ID 0
#define CLOSE_PACKAGE_NAME 1

#define REMOVE_PACKAGE 5
#define REMOVE_PACKAGE_COMPONENTS 3
#define REMOVE_PACKAGE_ID 0
#define REMOVE_PACKAGE_BOARD_NAME 1
#define REMOVE_PACKAGE_USERNAME 2

#define GIVE_ACCESS_PACKAGE 6
#define GIVE_ACCESS_PACKAGE_COMPONENTS 3
#define GIVE_ACCESS_PACKAGE_ID 0
#define GIVE_ACCESS_PACKAGE_BOARD_NAME 1
#define GIVE_ACCESS_PACKAGE_USERNAME 2

#define ADD_RFID_PACKAGE 7
#define ADD_RFID_PACKAGE_COMPONENTS 3
#define ADD_RFID_PACKAGE_ID 0
#define ADD_RFID_PACKAGE_BOARD_NAME 1
#define ADD_RFID_PACKAGE_RFID_NAME 2

#define BUFFER_LEN 300

char *package_components[MAX_PACKAGE_PARTS];
char buffer[BUFFER_LEN];

int get_package_components(const char *package, int number_of_parts, int package_len){
  int i = 0, j = 0, k = 2;
  while((i < number_of_parts) && (j < package_len)){
    buffer[j] = package[k];
    if(package[k] == 0){
      package_components[i] = strdup(buffer);
      i++;
      j = 0;
    }else{
      j++;
    }
    k++;
  }
  if(i == number_of_parts){
    return 1;
  }
  return 0;
}

char *admin_id;
char *board_name;

void handle_find_board(){
  char snum[5];
  USER_NODE *user, *aux;
  user = insert_node(package_components[FIND_BOARD_PACKAGE_ID], package_components[FIND_BOARD_PACKAGE_NAME]);
  UDP.beginPacket(UDP.remoteIP(), UDP.remotePort());
  itoa(user->has_access, snum, 10);
  UDP.write(snum);
  UDP.write((char) 0);
  UDP.write(board_name);
  UDP.write((char) 0);
  aux = user_list;
  if(strcmp(user->user_id,admin_id) == 0){
    itoa(user_list_len, snum, 10);
    UDP.write(snum);
    UDP.write((char) 0);
    while(aux != NULL){
      itoa(aux->has_access, snum, 10);
      UDP.write(snum);
      UDP.write((char) 0);
      UDP.write(aux->user_name);
      UDP.write((char) 0);
      Serial.println(aux->user_id);
      Serial.println(aux->user_name);
      aux = aux->next_node;
    }
  }
  UDP.endPacket();
  delay(10);
}

void handle_add_board(){
  USER_NODE *user;
  if (WiFi.status() != WL_CONNECTED) { 
    board_name = package_components[ADD_BOARD_PACKAGE_BOARD_NAME];
    admin_id = strdup(package_components[ADD_BOARD_PACKAGE_ADMIN_ID]);
    user = insert_node(package_components[ADD_BOARD_PACKAGE_ADMIN_ID], package_components[ADD_BOARD_PACKAGE_ADMIN_NAME]);
    user->has_access = 1;
    WiFi.begin(package_components[ADD_BOARD_PACKAGE_WIFI_NAME], package_components[ADD_BOARD_PACKAGE_WIFI_PASS]); 
  }
  free(package_components[ADD_BOARD_PACKAGE_WIFI_NAME]);
  free(package_components[ADD_BOARD_PACKAGE_WIFI_PASS]);
}

void handle_open(){
  if(strcmp(board_name, package_components[OPEN_PACKAGE_NAME]) == 0 && has_access(package_components[OPEN_PACKAGE_ID])){
    lock_status = OPEN;
    free(package_components[OPEN_PACKAGE_NAME]);
  }
}

void handle_close(){
  if(strcmp(board_name, package_components[CLOSE_PACKAGE_NAME]) == 0 && has_access(package_components[CLOSE_PACKAGE_ID])){
    lock_status = CLOSED;
    free(package_components[CLOSE_PACKAGE_NAME]);
  }
}

void handle_remove(){
  if(strcmp(board_name, package_components[REMOVE_PACKAGE_BOARD_NAME]) == 0 && strcmp(admin_id, package_components[REMOVE_PACKAGE_ID]) == 0){
    remove_node(package_components[REMOVE_PACKAGE_USERNAME]);
    free(package_components[REMOVE_PACKAGE_BOARD_NAME]);
    free(package_components[REMOVE_PACKAGE_ID]);
  }
}

void handle_give_access(){
  if(strcmp(board_name, package_components[GIVE_ACCESS_PACKAGE_BOARD_NAME]) == 0 && strcmp(admin_id, package_components[GIVE_ACCESS_PACKAGE_ID]) == 0){
    give_access(package_components[GIVE_ACCESS_PACKAGE_USERNAME]);
    free(package_components[GIVE_ACCESS_PACKAGE_BOARD_NAME]);
    free(package_components[GIVE_ACCESS_PACKAGE_ID]);
  }
}

char* rfid_name = NULL;
long rfid_time = 0;

void handle_add_rfid(){
  if(strcmp(board_name, package_components[ADD_RFID_PACKAGE_BOARD_NAME]) == 0 && strcmp(admin_id, package_components[ADD_RFID_PACKAGE_ID]) == 0){
    Serial.println(package_components[ADD_RFID_PACKAGE_RFID_NAME]);
    if(rfid_name != NULL){
      free(rfid_name);
    }
    rfid_name = package_components[ADD_RFID_PACKAGE_RFID_NAME];
    rfid_time = millis();
    free(package_components[ADD_RFID_PACKAGE_BOARD_NAME]);
    free(package_components[ADD_RFID_PACKAGE_ID]);
  }
}

void handle_package(const char *package, int size){
  switch(package[0]){
    case FIND_BOARD_PACKAGE:
      if(get_package_components(package, FIND_BOARD_PACKAGE_COMPONENTS, size)){
        Serial.println(package_components[FIND_BOARD_PACKAGE_ID]);
        handle_find_board();
      }
      break;
    case ADD_BOARD_PACKAGE:
      if(get_package_components(package, ADD_BOARD_PACKAGE_COMPONENTS, size)){
        handle_add_board();
      }
      break;
    case OPEN_PACKAGE:
      if(get_package_components(package, OPEN_PACKAGE_COMPONENTS, size)){
        handle_open();
      }
      break;
    case CLOSE_PACKAGE:
      if(get_package_components(package, CLOSE_PACKAGE_COMPONENTS, size)){
        handle_close();
      }
      break;
    case REMOVE_PACKAGE:
      if(get_package_components(package, REMOVE_PACKAGE_COMPONENTS, size)){
        handle_remove();
      }
      break;
    case GIVE_ACCESS_PACKAGE:
      if(get_package_components(package, GIVE_ACCESS_PACKAGE_COMPONENTS, size)){
        handle_give_access();
      }
      break;
    case ADD_RFID_PACKAGE:
      if(get_package_components(package, ADD_RFID_PACKAGE_COMPONENTS, size)){
        handle_add_rfid();
      }
      break;
  }
}

void connect() {
  String package;
  Serial.println("Connecting wifi ...");
  if (server.hasArg("plain")){
    package = server.arg("plain");
    Serial.println(package);
    handle_package(package.c_str(), package.length());
  }
  server.send(200, "text/plain", "OK!");
}

void handle_NotFound(){
  server.send(404, "text/plain", "Not found");
}

void startAPServer(){
  WiFi.softAP(ssid, password);
  IPAddress myIP = WiFi.softAPIP();
  Serial.print("Access Point IP:");
  Serial.println(myIP);
  
  server.on("/connect", HTTP_POST, connect);
  server.onNotFound(handle_NotFound);
  server.begin();
  Serial.println("HTTP Server Started");
}

void setup_pins(){
  pinMode(D3, OUTPUT); 
}

SoftwareSerial SWSerial( D2, D1 ); // RX, TX
 
PN532_SWHSU pn532swhsu( SWSerial );
PN532 nfc( pn532swhsu );
String tagId = "None", dispTag = "None";
byte nuidPICC[4];
 
void setup_nfc(void)
{
  nfc.begin();
  uint32_t versiondata = nfc.getFirmwareVersion();
  nfc.SAMConfig();
}

void setup() {
  Serial.begin(9600);
  pinMode(D3, OUTPUT); 
  setup_pins();

  startAPServer();

  setup_nfc();
}

void start_udp_server(){
  Serial.print("Starting UDP server ...");
  UDP.beginMulticast(WiFi.localIP(), broadcast_ip, UDP_PORT);
  UDP.begin(UDP_PORT);
  Serial.print("Listening on UDP port ");
  Serial.println(UDP_PORT);
}

void try_start_udp_server(){
  if (udp_server_status == NOT_RUNNING) { 
    Serial.println(F("WiFi connected!"));
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());
    start_udp_server();
    udp_server_status = RUNNING;
    WiFi.softAPdisconnect(true);
  }
}

void handle_udp_package(){
  char *package;
  int packetSize = UDP.parsePacket();
  if (packetSize) {
    Serial.print("Received packet! Size: ");
    Serial.println(packetSize); 
    package = (char*) calloc(packetSize + 1, sizeof(char));
    int len = UDP.read(package, packetSize);
    if (len > 0){
      package[len] = '\0';
    }
    Serial.print("Packet received: ");
    Serial.println(package);

    handle_package(package, len);

    free(package);
  }
}

String tagToString(byte id[4]) {
  String tagId = "";
  for (byte i = 0; i < 4; i++) {
    if (i < 3) tagId += String(id[i]) + ".";
    else tagId += String(id[i]);
  }
  return tagId;
}


#define RFID_PERIOD 800
#define ADD_RFID_PERIOD 30000
long now = 0, last_read = 0;

void readNFC() {
  boolean success;
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  
  uint8_t uidLength;    
  now = millis();
  if(now - last_read > RFID_PERIOD){                
    success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, &uid[0], &uidLength, 30);
    if (success) {
      Serial.print("UID Length: ");
      Serial.print(uidLength, DEC);
      Serial.println(" bytes");
      Serial.print("UID Value: ");
      for (uint8_t i = 0; i < uidLength; i++) {
        nuidPICC[i] = uid[i];
        Serial.print(" "); Serial.print(uid[i], DEC);
      }
      tagId = tagToString(nuidPICC);
      dispTag = tagId;
      Serial.print(F("tagId is : "));
      Serial.println(tagId);
      if(now - rfid_time <= ADD_RFID_PERIOD && rfid_name != NULL){
        Serial.print("add rfid:");
        Serial.print(tagId.c_str());
        Serial.print(rfid_name);
        Serial.println();
        USER_NODE *node;
        node = insert_node(strdup(tagId.c_str()), rfid_name);
        node->has_access = 1;
        rfid_name = NULL;
      }

      if(has_access(strdup(tagId.c_str()))) {
        if(lock_status == CLOSED){
          lock_status = OPEN;
        } else{
          lock_status = CLOSED;
        }
      }
    }
    last_read = now;
  }
}

void loop() {
  server.handleClient();
  
  if(lock_status == CLOSED) {
    digitalWrite(D3, HIGH); 
  } else {
    digitalWrite(D3, LOW); 
  }

  if (WiFi.status() == WL_CONNECTED) { 
    try_start_udp_server();
  }

  if(udp_server_status == RUNNING){
    handle_udp_package();
    delay(20);
    readNFC();
  }
}