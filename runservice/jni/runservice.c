#include <sys/types.h>
#include <unistd.h>
//#define _GNU_SOURCE
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "runservice.h"

ssize_t getline(char **buf, size_t *n, FILE *fp) {
    char c;
    int needed = 0;
    int maxlen = *n;
    char *buf_ptr = *buf;

    if (buf_ptr == NULL || maxlen == 0) {
        maxlen = 128;
        if ((buf_ptr = malloc(maxlen)) == NULL)
            return -1;
    }

    do {
        c = fgetc(fp);
        buf_ptr[needed++] = c;

        if (needed >= maxlen) {

            *buf = buf_ptr;
            buf_ptr = realloc(buf_ptr, maxlen *= 2);

            if (buf_ptr == NULL) {
                (*buf)[needed - 1] = '\0';
                return -1;
            }
        }

        if (c == EOF || feof(fp))
            return -1;

    } while (c != '\n');

    buf_ptr[needed] = '\0';
    *buf = buf_ptr;
    *n = maxlen;
    return needed;
}

int chk_process(const char *process_name)
{
  FILE   *stream;
  char   *line = NULL;
  size_t len = 0;
  ssize_t read_len;

  stream = popen( "ps", "r" );
  if (stream == NULL)
    return -1;

  int exists = 0;
  while ( (read_len = getline(&line, &len,  stream)) != -1)
  {
  	int len = strlen(line);
    char *cmd = line + len;
    while ( len >0 ) 
    {	
    	len--;
    	if ( *cmd == ' ')
    	{
    		cmd++;
    		break;
    	}
    	
    	cmd--;
  	}

    if( strncmp(cmd, process_name, strlen(process_name)) == 0 )
    {
      exists = 1;
      break;
    }
  }

  pclose( stream );
  if ( line != NULL )
    free(line);

  return exists;
}

void run_service(const char *process_name, const char *package_name, const char *activity_name, int interval_sec)
{
  while (1)
  {
    if ( chk_process(process_name) == 0)
    {
      char *pkg_activity_name = NULL;
      asprintf(&pkg_activity_name, "/system/bin/am start --user 0 -n %s/%s", package_name, activity_name);
      system(pkg_activity_name);
      free(pkg_activity_name);
    }

    sleep(interval_sec);
  }

  return;
}
