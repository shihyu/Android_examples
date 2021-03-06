#ifndef	__zealink_winlinux_define__
#define	__zealink_winlinux_define__
/////////////////////////////////////////////////////////////////////////////
#include	<stdio.h>
#include	<stdlib.h>
#include	<string.h>
#include	<math.h>
#include	<stdarg.h>
#include	<ctype.h>
#include	<errno.h>
#include	<fcntl.h>
#include	<sys/stat.h>
#include	<sys/types.h>

#if (defined WIN32) || (defined WIN64)
//#ifndef	LINUX | _WIN64
	#include	<share.h>
	#include	<time.h>
	#include	<io.h>
	#include	<sys/utime.h>
	#include	<winsock2.h>
	#include	<windows.h>
	#include	<tchar.h>
#else
	//#include <char.h>
	#include  <wchar.h>
	#include	<unistd.h>
	#include	<pthread.h>
	#include	<dirent.h>
	#include	<fnmatch.h>
	#include	<unistd.h>
	#include	<zlib.h>
	#include	<netdb.h>
	#include	<signal.h>
	#include	<getopt.h>
	#include	<termios.h>
	#include	<sysexits.h>
	#include	<utime.h>

	#include	<netinet/in.h>
	#include	<arpa/inet.h>
	// #include	<linux/keyboard.h>
	#include	<linux/fb.h>
	#include	<net/if.h>
	#include	<net/if_arp.h>

	#include	<sys/wait.h>
	#include	<sys/uio.h>
	#include	<sys/un.h>
	#include	<sys/socket.h>
	#include	<sys/ioctl.h>
	#include	<sys/mman.h>
	#include	<sys/io.h>
	#include  <sys/fcntl.h>
	#include	<sys/kd.h>
	#include	<sys/time.h>
	#include	<sys/param.h>
	#include	<sys/utsname.h>
#endif
#pragma pack(push,1)
/////////////////////////////////////////////////////////////////////////////
#ifdef _WIN32
	typedef	unsigned char		XBYTE;
	typedef	unsigned short		XWORD;
	typedef	long				XINT32;
	typedef	unsigned long		XDWORD;
	typedef	__int64				XINT64;
	typedef	unsigned __int64	XBIGINT;
#else
	typedef	unsigned char		XBYTE;
	typedef	unsigned short		XWORD;
	typedef	int					XINT32;
	typedef	unsigned int		XDWORD;
	typedef	long long			XINT64;
	typedef	unsigned long long	XBIGINT;
#endif

#if (defined WIN32) || (defined WIN64)
	typedef	HANDLE	HTHREAD;
	typedef	HANDLE	HPIPE;

	typedef	int	socklen_t;
	#define	NOVTABLE __declspec(novtable)
	#ifdef TEST_SOLUTION
		#define PRIVATE   public
	#else
		#define PRIVATE   private
	#endif

	#ifdef TEST_SOLUTION
		#define PROTECTED   public
	#else
		#define PROTECTED   protected
	#endif
#else	//Global Define(like win)
	typedef	pthread_t	HTHREAD;
	typedef	int		HPIPE[2];
	typedef XDWORD			WPARAM;
	typedef XINT32			LPARAM;
	typedef const void		*LPCVOID;
	typedef const char		*LPCSTR;
	typedef char			*LPSTR;
	typedef const wchar_t	*LPCWSTR;
	typedef wchar_t			*LPWSTR;
	typedef	__U32_TYPE		__time32_t;
	typedef unsigned char	*LPBYTE;
	typedef XDWORD			COLORREF;
	typedef void			*LPVOID;
	typedef void			*HANDLE;
	typedef void			*HINSTANCE;
	typedef XINT32			BOOL;
	typedef XINT32			LRESULT;
	typedef int				SOCKET;
	typedef void*			HWND;
	typedef short			SHORT;
	typedef char			TCHAR;
	typedef const char* LPCTSTR;
	typedef char* LPTSTR;
	const XINT32 MAX_PATH=260;

	#define	FILE_BEGIN		0
	#define FILE_CURRENT	1
	#define FILE_END		2

	#define INVALID_SOCKET  (SOCKET)-1
	#define	WINAPI
	#define	NOVTABLE

	#define MAKEWORD(a,b)	((XWORD)(((XBYTE)(a))|((XWORD)((XBYTE)(b)))<<8))
	#define MAKELONG(a,b)	((XINT32)(((XWORD)(a))|((XDWORD)((XWORD)(b)))<<16))
	#define LOWORD(l)		((XWORD)(l))
	#define HIWORD(l)		((XWORD)(((XDWORD)(l)>>16)&0xFFFF))
	#define LOBYTE(w)		((XBYTE)(w))
	#define HIBYTE(w)		((XBYTE)(((XWORD)(w)>>8)&0xFF))
	#define RGB(r,g,b)		((COLORREF)(((XBYTE)(r)|((XWORD)((XBYTE)(g))<<8))|(((XDWORD)(XBYTE)(b))<<16)))

//critical section macro:
	typedef XINT32 HRESULT;
	typedef	pthread_mutex_t CRITICAL_SECTION;
	#define	InitializeCriticalSection(hMutex)	pthread_mutex_init(hMutex,NULL)
	#define	DeleteCriticalSection(hMutex)		pthread_mutex_destroy(hMutex)
	#define	EnterCriticalSection(hMutex)		pthread_mutex_lock(hMutex)
	#define	LeaveCriticalSection(hMutex)		pthread_mutex_unlock(hMutex)
//map win func
	#define TlsAlloc() pthread_key_create()
	#define _vstprintf vsprintf
	#define _snprintf snprintf
	#define _sntprintf snprintf
	#define	lstrlenW(str) wcslen(str)
	#define	lstrlen(str) strlen(str)
	#define	lstrlenA(str) strlen(str)
	#define wsprintf	sprintf
	#define wsprintfW   swprintf
	#define wsprintfA	sprintf
	#define _istalnum   isalnum
	#define _istalpha	isalpha
	#define _istdigit	isdigit
	#define _atoi64 atoll
	#define _wtof(str)  wcstof(str, NULL)
	#define _wtoi64(str)    wcstoll (str, NULL, 10)
	#define _tfopen  fopen
	#define _wtol(str)  wcstol(str, NULL, 10)
	#define _wtoi(str)  wcstol(str, NULL, 10)
	#define	lstrcpy(str1,str2) strcpy(str1,str2)
	#define	lstrcpyA(str1,str2) strcpy(str1,str2)
	#define	lstrcpyn(str1,str2,size) strncpy(str1,str2,size)
	#define	lstrcpynW(str1,str2,size) wcscpy(str1,str2)
	#define	lstrcpynA(str1,str2,size) strncpy(str1,str2,size)
	#define strcpy_s(str1, size, str2) strncpy(str1, str2,size)
	//#define strcpy_s(str1, str2) strcpy(str1, str2)
	#define	lstrcat(str1,str2) strcat(str1,str2)
	#define	lstrcatA(str1,str2) strcat(str1,str2)
	#define	lstrcmp(str1,str2) strcmp(str1,str2)
	#define	lstrcmpA(str1,str2) strcmp(str1,str2)
	#define	lstrcmpi(str1,str2) strcasecmp(str1,str2)
	#define	lstrcmpiA(str1,str2) strcasecmp(str1,str2)
	#define	stricmp(str1,str2) strcasecmp(str1,str2)
	#define	strnicmp(str1,str2,n) strncasecmp(str1,str2,n)
	#define	closesocket(h)  ::close(h)
	#define	Sleep(lSeconds)	usleep(lSeconds*1000L)
	#define		ARRAYSIZE(a)  (sizeof(a)/sizeof(a[0]))
	#define  _T(x)	x
	#define TEXT(x) 	x
	typedef struct _LARGE_INTEGER
	{
		struct
		{
			XDWORD	LowPart;
			XINT32	HighPart;
		};
		XINT64	QuadPart;
	}LARGE_INTEGER;

#ifndef FILETIME
	typedef struct _FILETIME
	{
		XDWORD	dwLowDateTime;
		XDWORD	dwHighDateTime;
	}FILETIME,*PFILETIME,*LPFILETIME;
#endif

	typedef struct _SYSTEMTIME
	{
		XWORD	wYear;
		XWORD	wMonth;
		XWORD	wDayOfWeek;
		XWORD	wDay;
		XWORD	wHour;
		XWORD	wMinute;
		XWORD	wSecond;
		XWORD	wMilliseconds;
	}SYSTEMTIME,*PSYSTEMTIME,*LPSYSTEMTIME;
	typedef struct tagDBTIMESTAMP
    {
		short	year;
		XWORD	month;
		XWORD	day;
		XWORD	hour;
		XWORD	minute;
		XWORD	second;
		XDWORD	fraction;
    }DBTIMESTAMP;
#endif

#ifndef	TRUE
	#define	TRUE	1
#endif

#ifndef	FALSE
	#define	FALSE	0
#endif

#ifndef max
	#define	max(a,b)	(((a)>(b))?(a):(b))
#endif

#ifndef min
	#define min(a,b)	(((a)<(b))?(a):(b))
#endif

#ifndef	DELETE_PTR
	#define	DELETE_PTR(p) if(p){delete p;p=NULL;}
#endif

#ifndef	DELETE_PTRA
	#define	DELETE_PTRA(p) if(p){delete[] p;p=NULL;}
#endif

#ifndef	COPY_STRING
	#define	COPY_STRING(p1,p2)\
	lstrcpyn(p1,p2,sizeof(p1))
#endif

#ifndef	COPY_STRINGEX
	#define	COPY_STRINGEX(p1,p2)\
	memset(p1,0,sizeof(p1));\
	lstrcpyn(p1,p2,sizeof(p1));
#endif

#ifndef	WLSTAT_DEFINE
	#define	WLSTAT_DEFINE
	#ifdef	WIN32
		typedef	struct _stati64 STAT_STRU;
		#define	STAT_PROC _stati64
		#define	FSTAT_PROC _fstati64
	#else
		typedef	struct stat64 STAT_STRU;
		#define	STAT_PROC stat64
		#define	FSTAT_PROC fstat64
	#endif
#endif

#if (defined WIN32) || (defined WIN64)
	#define	WSA_LAST_ERROR	WSAGetLastError()
	#define	LAST_ERROR	GetLastError()

	#ifndef	CLOSE_HANDLE
		#define	CLOSE_HANDLE(h) if(h){CloseHandle(h);h=NULL;}
	#endif

	#ifndef	CLOSE_FILE
		#define	CLOSE_FILE(h) if(h!=INVALID_HANDLE_VALUE){CloseHandle(h);h=INVALID_HANDLE_VALUE;}
	#endif

	#ifndef	CLOSE_SOCKET
		#define	CLOSE_SOCKET(h)	if(h!=INVALID_SOCKET){closesocket(h);h=INVALID_SOCKET;}
	#endif

	#ifndef	THREAD_CREATE
		#define	THREAD_CREATE(AfxProc,Class,Method)\
		DWORD WINAPI AfxProc(LPVOID lpParam)\
		{\
			CoInitialize(NULL);\
			try\
			{\
				((Class*)lpParam)->Method();\
			}\
			catch(...)\
			{\
			}\
			CoUninitialize();\
			return(0);\
		}
	#endif

	#ifndef	THREAD_SAFE_CLOSE
		#define	THREAD_SAFE_CLOSE(h,tm)\
		if(h)\
		{\
			if(WaitForSingleObject(h,tm)!=WAIT_OBJECT_0)\
			{\
				TerminateThread(h,0);\
			}\
			CloseHandle(h);\
			h = NULL;\
		}
	#endif

	#define	DECLARE_FILEOPEN(fp,filename,openmode)	FILE*fp=_fsopen(filename,openmode,SH_DENYNO)
#else	//Linux
	#define	WM_USER		0x0400

	#define	WSA_LAST_ERROR	errno
	#define	LAST_ERROR	errno

	#ifndef	CLOSE_FILE
		#define	CLOSE_FILE(h) if(h!=NULL){fclose(h);h=NULL;}
	#endif

	#ifndef	CLOSE_SOCKET
		#define	CLOSE_SOCKET(h)	if(h>0){closesocket(h);h=-1;}
	#endif

	#ifndef	THREAD_CREATE
		#define	THREAD_CREATE(AfxProc,Class,Method)\
		void*AfxProc(void*lpParam)\
		{\
			try\
			{\
				((Class*)lpParam)->Method();\
			}\
			catch(...)\
			{\
			}\
			return(NULL);\
		}
	#endif

	#ifndef	THREAD_SAFE_CLOSE
		#define	THREAD_SAFE_CLOSE(h,tm)\
		if(h)\
		{\
			pthread_join(h,NULL);\
			h = 0;\
		}
	#endif

	#define	DECLARE_FILEOPEN(fp,filename,openmode)	FILE*fp=fopen(filename,openmode)
#endif
/////////////////////////////////////////////////////////////////////////////
#ifndef	ZERO_STRING
	#define	ZERO_STRING(s) memset(s,0,sizeof(s))
#endif

#ifndef	ROUNDUP_SIZE
	#define ROUNDUP_SIZE(size,amount)	(((ULONG)(size)+((amount)-1))&~((amount)-1))
#endif

#ifndef	CHECK_STRING_ENDNULL
	#define	CHECK_STRING_ENDNULL(s) s[sizeof(s)-1] = 0;
#endif

#ifdef	WIN32
	#ifdef	_UNICODE
		#define	__string_lwr__(s) _wcslwr(s)
		#define	__string_upr__(s) _wcsupr(s)
		#define	__bigint_to_string__(i,s,b) _i64tow(i,s,b)
		#define	__string_to_bigint__(s) _wtoi64(s)
		#define	__long_to_string__(i,s,b) _itow(i,s,b)
		#define	__string_to_long__(s) _wtol(s)
		#define	__string_to_double__(s) _wtof(s)
		#define	__string_search__(s,sub) wcsstr(s,sub)
		#define	__string_char__(s,c) wcschr(s,c)
		#define	__csprintf__ swprintf
	#else
		#define	__string_lwr__(s) _strlwr(s)
		#define	__string_upr__(s) _strupr(s)
		#define	__bigint_to_string__(i,s,b) _i64toa(i,s,b)
		#define	__string_to_bigint__(s) _atoi64(s)
		#define	__long_to_string__(i,s,b) _itoa(i,s,b)
		#define	__string_to_long__(s) atol(s)
		#define	__string_to_double__(s) atof(s)
		#define	__string_search__(s,sub) strstr(s,sub)
		#define	__string_char__(s,c) strchr(s,c)
		#define	__csprintf__ sprintf
	#endif
	#define	__stringA_to_bigint__(s) _atoi64(s)
#else
	#define	__string_lwr__(s)  _strlwr(s)
	#define	__string_upr__(s)  _strupr(s)
	#define	__bigint_to_string__(i,s,b)    CONVERT_BIGINT(s,i)
	#define	__string_to_bigint__(s)  atoll(s)
	#define	__long_to_string__(i,s,b)  sprintf(s, "%d", i) 
	#define	__string_to_long__(s) atol(s)
	#define	__string_to_double__(s) atof(s)
	#define	__string_search__(s,sub) strstr(s,sub)
	#define	__string_char__(s,c) strchr(s,c)
	#define	__csprintf__ sprintf
	#define	__stringA_to_bigint__(s) atoll(s)
#endif

//??????????
#ifndef	__SAFE_DELETE_GDIOBJ__	//????GDI????
#define	__SAFE_DELETE_GDIOBJ__(hObj)\
	if(hObj)\
	{	DeleteObject(hObj);\
		hObj = NULL;\
	}
#endif

#ifndef	__DELETE_OBJECTS__	//????GDI????????
#define __DELETE_OBJECTS__(objs)\
	for(int i=0;i<ARRAYSIZE(objs);++i)\
	{\
		__SAFE_DELETE_GDIOBJ__(objs[i])\
	}
#endif

#ifndef	COLORREF2RGB
	#define COLORREF2RGB(Color) (Color&0XFF00)|((Color>>16)&0XFF)|((Color<<16)&0XFF0000)
#endif

#ifndef	__CHECK_STRING__
	#define	__CHECK_STRING__(s) s[sizeof(s)/sizeof(s[0])-1] = 0
#endif

#ifndef	__define_protocol_header__
#define	__define_protocol_header__
typedef struct tagProtocolHead
{
	__time32_t	time_check;		//????????
	XDWORD	req_ans_command;	//req/ans command id
	XDWORD	source_size;		//source size(no compress)
	XDWORD	package_size;		//package size,if no compress,this must be 0
	XDWORD	self_param;			//HWND(x86)or other
}protocol_head,*pprotocol_head;
#endif

//??????????????
#ifndef	__udb_conninfo__
#define	__udb_conninfo__
enum
{
	DBMS_MSSQL=0,	//sql server 2000/2005/2008
	DBMS_MYSQL,		//mysql 4.x/5.x
	DBMS_ORACLE,	//oracle 8i/9i/10g/11g
	DBMS_DB2,		//ibm db2 8.x/9.x
	DBMS_COMMON,    //??????????
};
typedef struct tagUDBConnInfo
{
	XBYTE	dbtype;	
	//??????????:
	//0:sql server
	//1:mysql
	//2:oracle
	//3:db2

	XDWORD	policy;
	//????:
	//0x01 : ODBC????
	//0x02 : windows????????
	//0x04 : OCI????
	//0x08 : ORACLE??????MS????

	//0x0100 : excel

	char	dburl[32];		//????
	char	dbname[32];		//??????????
	char	dbuser[32];		//??????
	char	dbowner[32];	//??????
	char	dbpasswd[32];	//????
	char	utsmask[32];	//????????

	char	spaceTable[32];	//??????????
	char	spaceIndex[32];	//??????????
	char	spaceText[32];	//????????????

	char	reserved[32];	//????????
	char	dbScript[32];	//????????
}udbconn_info,*pudbconn_info;
#endif
///////////////////////////////////////////////////////////////////////////////////////////////////
enum
{
	__umymessage_netcommand__=WM_USER+0x9F01,	//????????
	__umessage_wndtrace__,				//????????????????
	__umessage_scrollbar__,				//??????????;????:wparam=1;????:wparam=0

	__umymessage_parentdraw__,			//????????????????????
	__umymessage_parent_mousemove__,	//????????????????????????
	__umymessage_parent_lbtndown__,		//????????????????????????????
	__umymessage_parent_rbtndown__,		//????????????????????????????
	__umymessage_parent_lbtnup__,		//????????????????????????????
	__umymessage_parent_rbtnup__,		//????????????????????????????
	__umymessage_parent_lbtndblclk__,	//????????????
	__umymessage_parent_keydown__,		//????????
	__umymessage_parent_lbtndblclk1__,	//????????????(??????????????)

	__umymessage_setstock__,
	__umymessage_setblock__,
	__umymessage_setindex__,
	__umymessage_setcode__,
	__umymessage_overlay__,				//????????
	__umymessage_uxsnormal__,
	__umymessage_setblock2__,
	__umymessage_setkeyindex__,

	__umymessage_parent_size_zoom_in__,	//????
	__umymessage_parent_size_zoom_out__,//????
	__umymessage_parent_close__,        //????????????????????
	__umymessage_preparentdraw__,		//????????????????????(??????????????????)
	__umymessage_afterdraw__,			//????????????
	__umymessage_char_curve_selected__,	//?????????? (chart)
	__umymessage_showwindows__,			//????????????????

	__umymessage_resetblock__,			//????????
	__umymessage_addnewblock__,			//????????
	__umymessage_deleteblock__,			//????????
	__umymessage_settype__,				//????????
	__umymessage_reload__,				//????????

	__umymessage_remove_module__,		//???????????? wParam=m_lIdentify

	__umymessage_setcode_by_navigate_plus__,	//??3???????????? wParam= PUMS_BLOCK_ITEM, lParam = m_pNavigatePlus->_szVDLItemParamName
	__umymessage_setblock_by_navigate_plus__,	//??3???????????? wParam= PUMS_BLOCK_ITEM, lParam = m_pNavigatePlus->_szVDLBlockParamName

	__umymessage_change_module__,	//?????????????????? wParam=m_lIdentify lParam=????????
	__umymessage_hq_to_minutes__,	//????????F5, wParam=m_lIdentify, lParam = stock
	__umymessage_hq_to_history__,	//????????F5, wParam=m_lIdentify, lParam = stock

	__umymessage_news_content__,

	__umymessage_update_friend_panel__,
	__umymessage_maxmin_panle__,	//?????????????? wParam=(1.???? 0.????) lParam= UMyModule9*
	__umymessage_size_panel___,		//????????
	__umymessage_changemax_panle__,	//??????????????  lParam= UMyModule9* ??????????????????????????????????????

	__umymessage_callback_setcode__=0x1522,

	__umymessage_init_umsdatashow__,
	UMS_SHOWWINDOW,			//????????/????????

};
/////////////////////////////////////////////////////////////////////////////////////////////////
#define	NUM_PRICE_LEVEL_L2	10	// Level2????????(10??)
#define	NUM_PRICE_LEVEL_L1	5	// Level1????????(5??)
typedef	struct	tagStockAttrib	//????????
{
	XBIGINT	Market:8;	//????
	XBIGINT	Type:8;		//??????:????
	XBIGINT	decNum:3;	//??????????????
	XBIGINT	unitBase:3;	//????????????
//??????(??????????????????????????????????,??????????)
	XBIGINT	A:1;	//GP:A??		JJ:??????????	ZQ:????			HG:??????    ????:????				????:NYSE
	XBIGINT	B:1;	//GP:B??		JJ:??????????	ZQ:????			HG:??????	 ????:????				????:AMEX
	XBIGINT	C:1;	//GP:????????	JJ:LOF			ZQ:????			HG:????		 ????:????				????:NASDAQ
	XBIGINT	D:1;	//GP:????		JJ:ETF			ZQ:????????		HG:????		 ????:??????????

	XBIGINT	E:1;	//GP:			JJ:FOF			ZQ:??????
	XBIGINT	F:1;	//GP:			JJ:????????????
	XBIGINT	G:1;	//GP:			JJ:
	XBIGINT	H:1;	//GP:			JJ:

	XBIGINT	I:1;	//GP:			JJ:
	XBIGINT	J:1;	//GP:			JJ:

	XBIGINT	unitContract:16;	//????????????
//????????
	XBIGINT	TS:2;		//1:????;2:????
	XBIGINT TRADETIME:5;//??????????????
	XBIGINT	Reserved:9;
}stock_attrib,*pstock_attrib;

typedef struct tagStockIndex	//????
{
	struct tagStockIndex	*pNext;	//??????(pagedown??????)
	struct tagStockIndex	*pPrev;	//??????(pageup??????)
	stock_attrib	attrib;		//????

	XBYTE	bcType1;
	XBYTE	bcType2;
	XBYTE	bcType3;

	char	ansiSymbol[10];
	char	ansiName[32];
	char	orgid[16];

	TCHAR	szSymbol[10];
	TCHAR	szName[32];
	TCHAR	szSpell[12];

	XINT32	minuteAccess;
	XINT32	verData;
	XINT32	stkIndex;

	XINT32	preClosepx;
	XINT32	highPx;
	XINT32	lowPx;
	XINT32	lastPrice;

	double	tradable_mkt_val;	//????????
	double	total_mkt_val;		//??????
	double	eps;				//????????
	double	BVPS;				//??????????
	XINT64	vol_5;				//5??????
}STOCK_INDEX,*PSTOCK_INDEX;

typedef struct tagMARKETDATA3	//????????????????(????????)
{
	XINT32	dataTimeStamp;					// ??????????????????163025????16:30:25

	XINT32	preClosePx;						// ??????
	XINT32	openPx;							// ????
	XINT32	highPx;							// ????
	XINT32	lowPx;							// ????

	XINT32	lastPrice;						// ????

	XINT64	bidSize[NUM_PRICE_LEVEL_L2];	// ??????
	XINT32	bidPx[NUM_PRICE_LEVEL_L2];		// ??????
	XINT64	offerSize[NUM_PRICE_LEVEL_L2];	// ??????
	XINT32	offerPx[NUM_PRICE_LEVEL_L2];	// ??????

	XINT32	numTrades;						// ????????
	XINT64	totalVolumeTrade;				// ????????
	XINT64	totalValueTrade;				// ??????????
	XBIGINT	volInside;						// ????????
	XBIGINT	volOutside;						// ????????

	union
	{
		XBIGINT	vol_5;	//5??????
		struct
		{
			XWORD NUM_STOCKS;	//??????????
			XWORD UP_NUM;		//????????
			XWORD DOWN_NUM;		//????????
			XWORD UN_CHANGE_NUM;	//????????
		}index;	//????
	};
	XINT64	totalBidQty;					// ????????
	XINT32	weightedAvgBidPx;				// ????????????????
	XINT32	altWeightedAvgBidPx;			// ????????????????????
	XINT64	totalOfferQty;					// ????????
	XINT32	weightedAvgOfferPx;				// ????????????????
	XINT32	altWeightedAvgOfferPx;			// ????????????????????

	XINT32	IOPV;							// IOPV????????
	XINT32	yieldToMaturity;				// ??????????
	XINT64	totalWarrantExecQty;			// ????????????????

	XINT64	warLowerPx;						// ????????????
	XINT64	warUpperPx;						// ????????????

	XDWORD	verData;		//??????????????
}market_data3,*pmarket_data3;

typedef struct tagUXSTimeSeed
{
	XDWORD		year:6;		//[year%60]
	XDWORD		month:4;
	XDWORD		day:5;
	XDWORD		hour:5;
	XDWORD		minute:6;
	XDWORD		second:6;
	XDWORD		seed;
}uxs_timeseed,*puxs_timeseed;

typedef struct tagUXSTreeData
{
	XBYTE	isGroup;			//????????
	XINT32	iconIndex;			//????ID
	XINT64	parentCode;			//??ID
	XINT64	thisCode;			//????ID
	union
	{
		XWORD	wName[32];		//????????:??????????
		char	szName[64];		//????????:??????????
	};
	union
	{
		XWORD	wInfo[64];		//????????
		char	szInfo[128];	//????????
	};
	XINT32	extLength;			//????????????
	XINT32	extLength2;			//????????2????
	XINT32	extLength3;			//????????3????
	XINT32	extLength4;			//????????4????
	XINT32	extLength5;			//????????3????
	XINT32	extLength6;			//????????4????
	char	reserved[40];		//????????
	char	dataBuffer[1];		//????????????
}uxs_treedata,*puxs_treedata;
/////////////////////////////////////////////////////////////////////////////////////////////////
const XDWORD	UERROR_NO=0;
const XDWORD	UERROR_UNKNOW=9999;
/////////////////////////////////////////////////////////////////////////////
//system error:
const XDWORD UERROR_MALLOC=101;		//malloc/new data error
const XDWORD UERROR_NETWORK=102;		//net work error
const XDWORD UERROR_NOTSUPPORT=103;	//not support
const XDWORD UERROR_SERIAL=104;		//error serial id
const XDWORD UERROR_SRVNOTRUN=105;	//service not run
const XDWORD UERROR_BUSY=106;		//system busy
const XDWORD UERROR_NODATA=107;		//??????????????????
/////////////////////////////////////////////////////////////////////////////
//public error:
const XDWORD UERROR_ACCOUNTEXIST=201;	//??????????
const XDWORD UERROR_ACCOUNTNOTEXIST=202;	//??????????
const XDWORD UERROR_PASSWD=203;			//passwd error
const XDWORD UERROR_NETWORKCARD=204;		//network card denied
const XDWORD UERROR_DISABLED=205;		//disabled error
const XDWORD UERROR_EXPIRED=206;			//expired error
const XDWORD UERROR_IPADDRESS=207;		//ipaddress error
const XDWORD UERROR_OVERMAX=208;			//over max
const XDWORD UERROR_DENIED=209;			//denied

const XDWORD UERROR_FILEOPEN=301;		//open file error
const XDWORD UERROR_FILESEEK=302;		//seek file error
const XDWORD UERROR_FILEREAD=303;		//read file error
const XDWORD UERROR_FILEWRITE=304;		//write file error
const XDWORD UERROR_QUOTA=305;			//dir quota

const XDWORD UERROR_DBCONNSRV=401;		//connect to database server
const XDWORD UERROR_DBREAD=402;			//read recordset error
const XDWORD UERROR_DBWRITE=403;			//write recordset error
const XDWORD UERROR_TABLENOTEXIST=404;	//????????
const XDWORD UERROR_UTSMASK=405;			//??????????????
const XDWORD UERROR_CREATETABLE=406;		//??????????
///////////////////////////////////////////////////////////////////////////////////////////////////
#pragma pack(pop)
#endif
