# Sets the minimum version of CMake required to build the native library.
#设置构建本地库所需的最低cmake版本
cmake_minimum_required(VERSION 3.4.1)
#设置库文件导出目录
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/lameLibs/${ANDROID_ABI})
#批量导入c文件和cpp文件
file(GLOB lame lamemp3/*.c)
file(GLOB lame2 lamemp3/*.cpp)
#批量导入头文件
include_directories(${CMAKE_CURRENT_LIST_DIR}/lamemp3)
# 创建和给一个库命名，可以设置为静态库
# 或动态库，并且提供它源码的相关路径
# 你可以定义多个库，然后cmake为你构建他们
# gradle会自动打包共享库到你的apk中
add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # 导入所有源文件
        native-lib.cpp
        ${lame}
        ${lame2}
        )

#所有指定的预构建库，并将路径定义为变量，用于后面引入
find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

#指令cmake需要链接到的库
target_link_libraries(
        #指定目标library
        native-lib
        #链接目标库到log库
        ${log-lib})