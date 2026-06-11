# Set minimum version
cmake_minimum_required(VERSION 3.4.1)

project(DemoJni)

# Specifies build list and target
add_library( # target name
             demo-jni

             # set lib type
             SHARED

             # source
             src/main/cpp/demo-native.cpp)

# include jni.h
include_directories(${ANDROID_NDK}/usr/include)


# link lib
find_library( # Define name of variable
              log-lib

              # Specified name
              log)

target_link_libraries( # target
                       demo-jni

                       # link lib list
                       ${log-lib})

# Support 16 KB page sizes
target_link_options(demo-jni PRIVATE "-Wl,-z,max-page-size=16384")
