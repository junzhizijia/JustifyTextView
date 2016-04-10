#JustifyTextView
TextView在换行时常常会出现参差不齐的情况，JustifyTextView就是为了解决这样的问题而来的。当然，这个自定义View主要用于学习研究，
请谨慎用于实际项目。\u0001

##中英文混排效果

####全角半角完全对齐
![](http://ww1.sinaimg.cn/mw690/b5405c76gw1f2s6s8dxjnj20dc0m8djf.jpg)

####仅全角对齐
![](http://ww3.sinaimg.cn/mw690/b5405c76gw1f2s7dtgd6xj20dc0m8q6l.jpg)

##英文效果
![](http://ww2.sinaimg.cn/mw690/b5405c76gw1f2s6r3dgocj20dc0m8jut.jpg)

##中文效果
![](http://ww2.sinaimg.cn/mw690/b5405c76gw1f2s8cpvoupj20dc0m8ac9.jpg)

##使用

####gradle
```
dependencies {
    compile 'io.github.leibnik:justifytextview:1.0.2'
}
```
####xml
```xml
<io.github.leibnik.justifytextview.JustifyTextView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        app:line_space="3px"
        app:character_space="0px"
        app:align_chars="true"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/test"/>
```

`app:line_space`：配置行间距
`app:character_space`：配置字符间距
`app:align_chars`：是否完全对齐字符，默认完全对齐，仅在文本包含CJK字符时有效

# License

    The MIT License (MIT)

    Copyright (c) 2016 leibnik

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.