project('college-length-estimator', 'cpp', default_options: ['cpp_std=c++17'], version: '0.1.0')

dependencies = dependency('qt6', modules : ['Core', 'Gui', 'Widgets'])
qt6_module = import('qt6')

subdir('src')
incldir = include_directories('include')
moc_files = qt6_module.compile_moc(headers: 'include/courseviewer.hpp', include_directories: incldir, dependencies: dependencies)

exe = executable('cle', sources, generated_ui_headers, moc_files, include_directories: incldir, dependencies: dependencies)

#test('compiles', exe)
