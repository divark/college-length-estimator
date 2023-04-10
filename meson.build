project('college-length-estimator', 'cpp', version: '0.1.0')

dependencies = dependency('qt6', modules : 'Widgets')

subdir('src')
incldir = include_directories('include')

exe = executable('cle', sources, include_directories: incldir, dependencies: dependencies)

test('compiles', exe)
